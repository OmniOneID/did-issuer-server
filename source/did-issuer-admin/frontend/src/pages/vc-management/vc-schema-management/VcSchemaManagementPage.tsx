import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useMemo, useState } from 'react';
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
  // const [rows, setRows] = useState<{ id: string | number }[]>([]);
  const [totalRows, setTotalRows] = useState<number>(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<VcSchemaRow[]>([]);

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const selectedRowData = useMemo(
    () => Array.isArray(rows) ? rows.find(row => row.id === selectedRow) || null : null,
    [rows, selectedRow]
  );

  useEffect(() => {
    setLoading(true);
    fetchVcSchema(paginationModel.page, paginationModel.pageSize, null, null)
      .then((response) => {
        setRows(response.data.content);
        setTotalRows(response.data.total);
      })
      .catch((error) => {
        console.error("Failed to retrieve namespaces. ", error);
        navigate('/error', { state: { message: formatErrorMessage(error, "Failed to retrieve Namespaces") } });
      })
      .finally(() => setLoading(false));
  }, [paginationModel]);


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
            console.error("Failed to delete VC Schema. ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to delete VC Schema") } });
          })
          .finally(() => setLoading(false));
      }
    }
};


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
          // onEdit={() => {
          //   if (selectedRowData) {
          //     navigate(`/vc-management/vc-schema-management/vc-schema-edit/${selectedRowData.id}`);
          //   }
          // }}
          onRegister={() => navigate('/vc-management/vc-schema-management/vc-schema-registration')}
          // onDelete={handleDelete}
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
export default VcSchemaManagementPage 