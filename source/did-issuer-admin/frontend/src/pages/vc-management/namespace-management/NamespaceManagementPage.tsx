import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { deleteNamespace, fetchNamespaces } from '../../../apis/vc-management-api';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

type NamespaceRow = {
  id: string | number;
  namespaceId: string;
  name: string;
  vcSchemaCount: number;
  createdAt: string;
};

const NamespaceManagementPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState<boolean>(false);
  const [totalRows, setTotalRows] = useState<number>(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<NamespaceRow[]>([]);
  const [searchText, setSearchText] = useState<string>('');
  const [selectedSearch, setSelectedSearch] = useState<string>('namespaceId');

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const selectedRowData = useMemo(
    () => Array.isArray(rows) ? rows.find(row => row.id === selectedRow) || null : null,
    [rows, selectedRow]
  );

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchNamespaces(
        paginationModel.page,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.total);
    } catch (err) {
      console.error("Failed to retrieve namespaces. ", err);
      navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve Namespaces.") } });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

  const getData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchNamespaces(
        0,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.total);
      setPaginationModel((prev) => ({ ...prev, page: 0 }));
    } catch (err) {
      setLoading(false);
      console.error("Failed to retrieve namespaces. ", err);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, 'Failed to retrieve Namespaces.'),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleUpdate = async () => {
    if (!selectedRowData) return;

    if (selectedRowData.vcSchemaCount > 0) {
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: 'This namespace is in use by one or more VC schemas and cannot be updated.',
        isModal: true,
      });
      return;
    }

    navigate(`/vc-management/namespace-management/namespace-edit/${selectedRowData.id}`);
  };

  const handleDelete = async () => {
    if (!selectedRowData) return;

    if (selectedRowData.vcSchemaCount > 0) {
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: 'This namespace is in use by one or more VC schemas and cannot be deleted.',
        isModal: true,
      });
      return;
    }

    const id = selectedRowData?.id as number;
    if (id) {
      const result = await dialogs.open(CustomConfirmDialog, {
        title: 'Confirmation',
        message: 'Are you sure you want to delete Namespace?',
        isModal: true,
      });

      if (result) {
        setLoading(true);
        deleteNamespace(id)
          .then(() => {
            setLoading(false);
            getData();
            dialogs.open(CustomDialog, {
              title: 'Notification',
              message: 'Namespace delete completed.',
              isModal: true,
            }, {
              onClose: async () => {
                setPaginationModel(prev => ({ ...prev }));
              },
            });
          })
          .catch((err) => {
            setLoading(false);
            console.error("Failed to delete Namespace. ", err);
            dialogs.open(CustomDialog, {
              title: 'Notification',
              message: formatErrorMessage(err, "Failed to delete Namespace"),
              isModal: true,
            });
          });
      }
    }
  };

  const handleSearch = useCallback(
    async (field: string, text: string) => {
      const trimmed = text.trim();
      if (!trimmed) return;
      setSelectedSearch(field);
      setSearchText(trimmed);
      setPaginationModel((prev) => ({ ...prev, page: 0 }));
    },
    []
  );

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    margin: 'auto',
    marginTop: theme.spacing(1),
    padding: theme.spacing(3),
    border: 'none',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: '#ffffff',
    boxShadow: '0px 4px 8px 0px #0000001A',
  })), []);

  const StyledSubTitle = useMemo(() => styled(Typography)({
      textAlign: 'left',
      fontSize: '24px',
      fontWeight: 700,
  }), []);

  return (
    <>
      <FullscreenLoader open={loading} />
      <StyledContainer>
        <StyledSubTitle>Namespace Management</StyledSubTitle>
        <CustomDataGrid
            rows={rows}
            columns={[
              { field: 'namespaceId', headerName: "ID", width: 200},
              {
                field: 'name',
                headerName: "Name",
                width: 200,
                renderCell: (params) => (
                  <Link
                    component="button"
                    variant='body2'
                    onClick={() => navigate(`/vc-management/namespace-management/${params.row.id}`)}
                    sx={{ cursor: 'pointer', color: 'primary.main' }}
                  >
                    {params.value}
                  </Link>),
              },
              { field: 'vcSchemaCount', headerName: "VC Schema Count", width: 150 },
              { field: 'createdAt', headerName: "Registered At", width: 200},
            ]}
            selectedRow={selectedRow}
            setSelectedRow={setSelectedRow}
            onEdit={handleUpdate}
            onRegister={() => navigate('/vc-management/namespace-management/namespace-registration')}
            onDelete={handleDelete}
            additionalButtons={[]}
            paginationMode="server"
            totalRows={totalRows}
            paginationModel={paginationModel}
            setPaginationModel={setPaginationModel}
            enableSearch={true}
            searchText={searchText}
            setSearchText={setSearchText}
            selectedSearch={selectedSearch}
            setSelectedSearch={setSelectedSearch}
            searchOptions={[
              { value: 'namespaceId', label: 'ID' },
              { value: 'name', label: 'Name' },
            ]}
            onSearch={handleSearch}
            onRefresh={getData}
          />
        </StyledContainer>
    </>
  )
}

export default NamespaceManagementPage
