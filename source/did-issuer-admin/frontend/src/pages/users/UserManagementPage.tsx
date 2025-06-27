import { Box, Button, Icon, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchNamespaces } from '../../apis/vc-management-api';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { fetchUserInfos } from '../../apis/admin-api';
import { formatErrorMessage } from '../../utils/error-handler';

type Props = {}

type UserInfoRow = {
  id: string | number;
  did: string;
  vcSchemaName: string;
  createdAt: string;
  updatedAt: string;
};

const UserManagementPage = (props: Props) => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState<boolean>(false);
  // const [rows, setRows] = useState<{ id: string | number }[]>([]);
  const [totalRows, setTotalRows] = useState<number>(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<UserInfoRow[]>([]);

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
    fetchUserInfos(paginationModel.page, paginationModel.pageSize, null, null)
      .then((response) => {
        setRows(response.data.content);
        setTotalRows(response.data.total);
      })
      .catch((error) => {
        console.error("Failed to retrieve User Infos. ", error);
        navigate('/error', { state: { message: formatErrorMessage(error, "Failed to retrieve User Infos.") } });
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
        <StyledSubTitle>User Management</StyledSubTitle>
        <CustomDataGrid
          rows={rows}
          columns={[
            {
              field: 'did',
              headerName: "DID",
              width: 200,
            },
            { field: 'vcSchemaName', headerName: "VC Schema", width: 200 },
            { field: 'createdAt', headerName: "Registered At", width: 200 },
            { field: 'updatedAt', headerName: "Updated At", width: 200 },
            {
              field: "",
              headerName: "View",
              width: 150,
              renderCell: (params) => (
                <Box sx={{
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  height: '100%',
                  width: '100%',
                }}>
                  <Button
                    variant="contained"
                    size="small"
                    onClick={() => navigate(`/users/user-management/${params.row.id}`)}
                  >
                    View
                  </Button>
                </Box>
              )
            }
          ]}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          // onEdit={() => {
          //   if (selectedRowData) {
          //     navigate(`/users/user-management/user-edit/${selectedRowData.id}`);
          //   }
          // }}
          // onRegister={() => navigate('/users/user-management/user-registration')}
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

export default UserManagementPage