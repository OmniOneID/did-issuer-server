import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';
import { fetchCredentialDefinitions, postReRegisterDefinition, postReRegisterSchema } from '../../../apis/zkp_management-api';

type ZkpNamespaceRow = {
  id: number;
  definitionId: string;
  schemaName: string;
  version: string;
  tag: string;
  status: string;
  createdAt: string;
  updatedAt: string;
};

const CredentialDefinitionManagementPage = () => {
  const navigate = useNavigate();
  const dialogs = useDialogs();

  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<ZkpNamespaceRow[]>([]);
  const [totalRows, setTotalRows] = useState(0);
  const [selectedRow, setSelectedRow] = useState<string| number | null>(null);

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const selectedRowData = useMemo(
    () => Array.isArray(rows) ? rows.find(row => row.id === selectedRow) || null : null,
    [rows, selectedRow]
  );

  const hansdleReRegisterAll = async () => {

      setLoading(true);
      try {
        await postReRegisterDefinition();

        setLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Completed re-register ZKP Credential Definition',
          isModal: true,
        }, {
          onClose: async () => navigate('/zkp-management/credential-definition-management'),
        });
      } catch (error) {
        setLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: formatErrorMessage(error, "Failed to re-register ZKP Credential Definition."),
          isModal: true,
        });
      }
  };

  useEffect(() => {
    setLoading(true)
    fetchCredentialDefinitions(paginationModel.page, paginationModel.pageSize, null, null)
    .then((response) => {
      setLoading(false);
      setRows(response.data.content);
      setTotalRows(response.data.total);
    })
    .catch((err) => {
      setLoading(false);
      console.error("Failed to retrieve credential definitions. ", err);
      dialogs.open(CustomDialog, {
          title: 'Notification',
          message: formatErrorMessage(err, "Failed to fetch credential definition list."),
          isModal: true,
      });
    });
  }, [paginationModel]);

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    width: '1100',
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
        <StyledSubTitle>Credential Definition Management</StyledSubTitle>
        <CustomDataGrid
            rows={rows}
            columns={[
              { field: 'definitionId', headerName: 'Definition ID', width: 250,
                  renderCell: (params) => (
                  <Link
                    component="button"
                    variant='body2'
                    onClick={() => navigate(`/zkp-management/credential-definition-management/${params.row.id}`)}
                    sx={{ cursor: 'pointer', color: 'primary.main' }}
                  >
                    {params.value}
                  </Link>
                ),
               },
              { field: 'schemaName', headerName: 'Schema Name', width: 120 },
              { field: 'version', headerName: 'Version', width: 100 },
              { field: 'tag', headerName: 'Tag', width: 100 },
              { field: 'status', headerName: 'Status', width: 230 },
              { field: 'createdAt', headerName: 'Registered At', width: 150 },
              { field: 'updatedAt', headerName: 'Updated At', width: 150 },
            ]}
            selectedRow={selectedRow}
            setSelectedRow={setSelectedRow}
            onRegister={() => navigate('/zkp-management/credential-definition-management/credential-definition-registration')}
            paginationMode="server"
            totalRows={totalRows}
            paginationModel={paginationModel}
            setPaginationModel={setPaginationModel}
            additionalButtons={[
              { label: 'Re-register all', onClick: () => hansdleReRegisterAll(), color: 'primary' },
            ]}
          />
      </StyledContainer>
    </>
  )
}

export default CredentialDefinitionManagementPage