import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchZkpNamespaces, zkpDeleteNamespace } from '../../../apis/zkp_management-api';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';

type ZkpNamespaceRow = {
  id: number;
  namespaceId: string;
  name: string;
  schemaCount: number;
  createdAt: string;
  updatedAt: string;
};

const ZkpNamespaceManagementPage = () => {
  const navigate = useNavigate();
  const dialogs = useDialogs();

  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<ZkpNamespaceRow[]>([]);
  const [totalRows, setTotalRows] = useState(0);
  const [selectedRow, setSelectedRow] = useState<string| number | null>(null);
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
      const response = await fetchZkpNamespaces(
        paginationModel.page,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.total);
    } catch (err) {
      console.error("Failed to retrieve zkp namespaces. ", err);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, "Failed to fetch zkp namespace list."),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  const getData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchZkpNamespaces(
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
      console.error("Failed to retrieve zkp namespaces. ", err);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, 'Failed to fetch zkp namespace list.'),
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

    if (selectedRowData.schemaCount > 0) {
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: 'This namespace is in use by one or more schemas and cannot be updated.',
        isModal: true,
      });
      return;
    }

    navigate(`/zkp-management/zkp-namespace-management/zkp-namespace-edit/${selectedRowData.id}`);

  }

  const handleDelete = async () => {
    if (!selectedRowData) return;

    if (selectedRowData.schemaCount > 0) {
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: 'This namespace is in use by one or more schemas and cannot be deleted.',
        isModal: true,
      });
      return;
    }

    const id = selectedRowData?.id as number;
    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to delete this ZKP Namespace?',
      isModal: true,
    });

    if (result) {
      setLoading(true);
      zkpDeleteNamespace(id)
          .then(() => {
            setLoading(false);
            getData();
            dialogs.open(CustomDialog, {
              title: 'Notification',
              message: 'ZKP Namespace delete completed.',
              isModal: true,
            }, {
              onClose: async () => {
                setPaginationModel(prev => ({ ...prev }));
              },
            });
          })
          .catch((err) => {
            setLoading(false);
            console.error("Failed to delete ZKP Namespace. ", err);
            dialogs.open(CustomDialog, {
              title: 'Notification',
              message: formatErrorMessage(err, "Failed to delete ZKP Namespace."),
              isModal: true,
            });
          })

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
    backgroundColor: '#ffffff',
    borderRadius: theme.shape.borderRadius,
    boxShadow: '0px 4px 8px rgba(0,0,0,0.1)',
  })), []);

  const StyledSubTitle = useMemo(() => styled(Typography)({
    fontSize: '24px',
    fontWeight: 700,
    textAlign: 'left',
  }), []);

  return (
    <>
      <FullscreenLoader open={loading} />
      <StyledContainer>
        <StyledSubTitle>ZKP Namespace Management</StyledSubTitle>
        <CustomDataGrid
          rows={rows}
          columns={[
            { field: 'namespaceId', headerName: 'ID', width: 240 },
            { field: 'name', headerName: 'Name', width: 200,
              renderCell: (params) => (
                <Link
                  component="button"
                  variant='body2'
                  onClick={() => navigate(`/zkp-management/zkp-namespace-management/${params.row.id}`)}
                  sx={{ cursor: 'pointer', color: 'primary.main' }}
                >
                  {params.value}
                </Link>
              ),

             },
            { field: 'schemaCount', headerName: 'Schema Count', width: 150 },
            { field: 'createdAt', headerName: 'Registered At', width: 200 },
            { field: 'updatedAt', headerName: 'Updated At', width: 200 },
          ]}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          onEdit={handleUpdate}
          onRegister={() => navigate('/zkp-management/zkp-namespace-management/namespace-registration')}
          onDelete={handleDelete}
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
  );
};

export default ZkpNamespaceManagementPage;
