import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchIssuedVc } from '../../apis/vc-management-api';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { fetchUserInfos } from '../../apis/admin-api';

type Props = {}

type UserInfoRow = {
  id: string | number;
  did: string;
  vcSchemaName: string;
  createdAt: string;
  updatedAt: string;
};

const IssuedVcManagementPage = (props: Props) => {
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

  const selectedRowData = useMemo(() => {
    return rows.find(row => row.id === selectedRow) || null;
  }, [rows, selectedRow]);
  
  useEffect(() => {
    setLoading(true);
    fetchIssuedVc(paginationModel.page, paginationModel.pageSize, null, null)
      .then((response) => {
        setRows(response.data.content);
        setTotalRows(response.data.totalElements);
      })
      .catch((error) => {
        console.error("Failed to retrieve User Infos. ", error);
        navigate('/error', { state: { message: `Failed to retrieve User Infos: ${error}` } });
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
      <StyledSubTitle>Issued VC Management</StyledSubTitle>
      <CustomDataGrid 
          rows={rows} 
          columns={[
            { 
              field: 'vcId', 
              headerName: "VC ID", 
              width: 300,
              renderCell: (params) => (
                <Link 
                  component="button"
                  variant='body2'
                  onClick={() => navigate(`/issued-vcs/issued-vc-management/${params.row.id}`)}
                  sx={{ cursor: 'pointer', color: 'primary.main' }}
                >
                  {params.value}
                </Link>),
            },
            { field: 'did', headerName: "DID", width: 150},
            // { field: 'vcSchemaName', headerName: "VC Schema", width: 200},
            // { field: 'status', headerName: "Status", width: 100},
            { field: 'createdAt', headerName: "Registered At", width: 150},
            
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

export default IssuedVcManagementPage