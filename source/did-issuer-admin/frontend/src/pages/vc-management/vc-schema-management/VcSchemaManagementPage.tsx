import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { deleteVcSchema, fetchVcSchema } from '../../../apis/vc-management-api';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}


type VcSchemaRow = {
  id: string | number;
  vcSchemaId: string;
  title: string;
  createdAt: string;
  updatedAt: string;
};


const VcSchemaManagementPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState<boolean>(false);
  const [totalRows, setTotalRows] = useState<number>(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<VcSchemaRow[]>([]);
  const [searchText, setSearchText] = useState<string>('');
  const [selectedSearch, setSelectedSearch] = useState<string>('vcSchemaId');

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
      const response = await fetchVcSchema(
        paginationModel.page,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.total);
    } catch (err) {
      console.error("Failed to retrieve VC schemas. ", err);
      navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve VC schemas") } });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

  const getData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchVcSchema(
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
      console.error("Failed to retrieve VC schemas. ", err);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, 'Failed to retrieve VC schemas.'),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleDelete = async () => {
    const id = selectedRowData?.id as number;
    if (id) {
      const result = await dialogs.open(CustomConfirmDialog, {
        title: 'Confirmation',
        message: 'Are you sure you want to delete VC Schema?',
        isModal: true,
      });

      if (result) {
        setLoading(true);
        deleteVcSchema(id)
          .then(() => {
            setLoading(false);
            getData();
            dialogs.open(CustomDialog, {
              title: 'Notification',
              message: 'VC Schema delete completed.',
              isModal: true,
            }, {
              onClose: async () => {
                setPaginationModel(prev => ({ ...prev }));
              },
            });
          })
          .catch((err) => {
            setLoading(false);
            console.error("Failed to delete VC Schema. ", err);
            dialogs.open(CustomDialog, {
              title: 'Notification',
              message: formatErrorMessage(err, "Failed to delete VC Schema"),
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
        <StyledSubTitle>VC Schema Management</StyledSubTitle>
        <CustomDataGrid
          rows={rows}
          columns={[
            { field: 'vcSchemaId', headerName: "ID", width: 200 },
            {
              field: 'title',
              headerName: "Title",
              width: 200,
              renderCell: (params) => (
                <Link
                  component="button"
                  variant='body2'
                  onClick={() => navigate(`/vc-management/vc-schema-management/${params.row.id}`)}
                  sx={{ cursor: 'pointer', color: 'primary.main' }}
                >
                  {params.value}
                </Link>),
            },
            { field: 'createdAt', headerName: "Registered At", width: 200 },
            { field: 'updatedAt', headerName: "Updated At", width: 200 },
          ]}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          onDelete={handleDelete}
          onRegister={() => navigate('/vc-management/vc-schema-management/vc-schema-registration')}
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
            { value: 'vcSchemaId', label: 'ID' },
            { value: 'title', label: 'Title' },
          ]}
          onSearch={handleSearch}
          onRefresh={getData}
        />
      </StyledContainer>
    </>
  )
}
export default VcSchemaManagementPage
