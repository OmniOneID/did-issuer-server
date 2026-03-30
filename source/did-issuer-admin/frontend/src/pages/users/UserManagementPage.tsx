import { Box, Button, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { fetchUserInfos } from '../../apis/admin-api';
import CustomDialog from '../../components/dialog/CustomDialog';
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
  const dialogs = useDialogs();
  const [loading, setLoading] = useState<boolean>(false);
  const [totalRows, setTotalRows] = useState<number>(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<UserInfoRow[]>([]);
  const [searchText, setSearchText] = useState<string>('');
  const [selectedSearch, setSelectedSearch] = useState<string>('did');

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
      const response = await fetchUserInfos(
        paginationModel.page,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.total);
    } catch (err) {
      console.error("Failed to retrieve User Infos. ", err);
      navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve User Infos.") } });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

  const getData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchUserInfos(
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
      console.error("Failed to retrieve User Infos. ", err);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, 'Failed to retrieve User Infos.'),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

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
            { value: 'did', label: 'DID' },
            { value: 'vcSchemaId', label: 'VC Schema ID' },
          ]}
          onSearch={handleSearch}
          onRefresh={getData}
        />
      </StyledContainer>
    </>
  )
}

export default UserManagementPage
