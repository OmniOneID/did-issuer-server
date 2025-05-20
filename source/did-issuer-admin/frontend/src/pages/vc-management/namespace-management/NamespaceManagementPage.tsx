import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useMemo, useState } from 'react';
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
  createdAt: string;
};

const NamespaceManagementPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState<boolean>(false);
  // const [rows, setRows] = useState<{ id: string | number }[]>([]);
  const [totalRows, setTotalRows] = useState<number>(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<NamespaceRow[]>([]);

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const selectedRowData = useMemo(
    () => Array.isArray(rows) ? rows.find(row => row.id === selectedRow) || null : null,
    [rows, selectedRow]
  );

  const handleDelete = async () => {
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
            console.error("Failed to delete Namespace. ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to delete Namespace") } });
          })
          .finally(() => setLoading(false));
      }
    }
};
  
  useEffect(() => {
    setLoading(true);
    fetchNamespaces(paginationModel.page, paginationModel.pageSize, null, null)
      .then((response) => {
        setRows(response.data.content);
        setTotalRows(response.data.totalElements);
      })
      .catch((error) => {
        console.error("Failed to retrieve namespaces. ", error);
        navigate('/error', { state: { message: formatErrorMessage(error, "Failed to retrieve Namespaces.") } });
      })
      .finally(() => setLoading(false));
  }, [paginationModel]);

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
              { field: 'createdAt', headerName: "Registered At", width: 200},
            ]} 
            selectedRow={selectedRow} 
            setSelectedRow={setSelectedRow}
            onEdit={() => {
              if (selectedRowData) {
                navigate(`/vc-management/namespace-management/namespace-edit/${selectedRowData.id}`);
              }
            }}
            onRegister={() => navigate('/vc-management/namespace-management/namespace-registration')}
            onDelete={handleDelete}
            additionalButtons={[
            
            ]}
            paginationMode="server" 
            totalRows={totalRows} 
            paginationModel={paginationModel} 
            setPaginationModel={setPaginationModel} 
          />
        </StyledContainer>
    </>
  )
}

export default NamespaceManagementPage