import { Box, Button, Dialog, Link, MenuItem, Select, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchIssuedVc, updateIssuedVcStatus } from '../../apis/vc-management-api';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import CustomDialog from '../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../utils/error-handler';

type Props = {}

type UserInfoRow = {
  id: string | number;
  vcId: string;
  did: string;
  vcSchemaId: string;
  status: string;
  createdAt: string;
  updatedAt: string;
};

const IssuedVcManagementPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState<boolean>(false);
  const [totalRows, setTotalRows] = useState<number>(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<UserInfoRow[]>([]);
  const [searchText, setSearchText] = useState<string>('');
  const [selectedSearch, setSelectedSearch] = useState<string>('vcId');

  const [statusDialogOpen, setStatusDialogOpen] = useState(false);
  const [newStatus, setNewStatus] = useState<string>('ACTIVE');

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
      const response = await fetchIssuedVc(
        paginationModel.page,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.total);
    } catch (err) {
      console.error("Failed to retrieve issued VCs. ", err);
      navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve issued VCs.") } });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

  const getData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchIssuedVc(
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
      console.error("Failed to retrieve issued VCs. ", err);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, 'Failed to retrieve issued VCs.'),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const getAvailableStatuses = (currentStatus: string): string[] => {
    switch (currentStatus) {
      case 'ACTIVE':
        return ['INACTIVE', 'REVOKED'];
      case 'INACTIVE':
        return ['ACTIVE', 'REVOKED'];
      case 'REVOKED':
      default:
        return [];
    }
  };

  const handleStatusEdit = () => {
    if (!selectedRowData || selectedRowData.status === 'REVOKED') {
      alert('The REVOKED state cannot be changed to any other state.');
      return;
    }

    setNewStatus(getAvailableStatuses(selectedRowData.status)[0]);
    setStatusDialogOpen(true);
  };

  const updateVcStatus = async (id: string | number, status: string) => {
    try {
      setLoading(true);
      let body = {
        vcId: id,
        vcStatus: status
      }
      await updateIssuedVcStatus(body);

      const response = await fetchIssuedVc(
        paginationModel.page,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.total);

    } catch (error) {
      navigate('/error', { state: { message: formatErrorMessage(error, "Failed to update VC status.") } });
    } finally {
      setLoading(false);
      setStatusDialogOpen(false);
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
        <StyledSubTitle>Issued VC Management</StyledSubTitle>
        <CustomDataGrid
          rows={rows}
          columns={[
            {
              field: 'vcId',
              headerName: "VC ID",
              width: 200,
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
            { field: 'did', headerName: "DID", width: 150 },
            { field: 'vcSchemaId', headerName: "VC Schema ID", width: 400 },
            { field: 'status', headerName: "Status", width: 100 },
            { field: 'createdAt', headerName: "Registered At", width: 150 },
          ]}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          onEdit={() => {
            if (selectedRowData) {
              handleStatusEdit();
            }
          }}
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
            { value: 'vcId', label: 'VC ID' },
            { value: 'did', label: 'DID' },
            { value: 'vcSchemaId', label: 'VC Schema ID' },
          ]}
          onSearch={handleSearch}
          onRefresh={getData}
        />
      </StyledContainer>
      <Dialog open={statusDialogOpen} onClose={() => setStatusDialogOpen(false)}>
        <Box sx={{ p: 3, minWidth: 300 }}>
          <Typography variant="h6" gutterBottom>Change VC Status</Typography>

          <Box sx={{ mb: 2 }}>
            <Select
              fullWidth
              value={newStatus}
              onChange={(e) => setNewStatus(e.target.value)}
            >
              {getAvailableStatuses(selectedRowData?.status ?? '').map((status) => (
                <MenuItem key={status} value={status}>{status}</MenuItem>
              ))}
            </Select>
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 2 }}>
            <Button
              variant="outlined"
              onClick={() => setStatusDialogOpen(false)}
              sx={{ flex: 1 }}
            >
              Cancel
            </Button>
            <Button
              variant="contained"
              onClick={() => {
                updateVcStatus(selectedRowData?.vcId ?? '', newStatus);
                setStatusDialogOpen(false);
                getData();
              }}
              sx={{ flex: 1 }}
            >
              Confirm
            </Button>
          </Box>
        </Box>
      </Dialog>
    </>
  )
}

export default IssuedVcManagementPage
