import {
  Box, Button, Dialog, DialogActions, DialogContent, DialogTitle,
  FormControl, InputLabel, Link, MenuItem, Select, styled, TextField, Typography,
} from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import {
  changeOid4vciCredentialStatus,
  fetchOid4vciIssuedCredentials,
} from '../../apis/oid4vci-issued-credentials-api';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../utils/error-handler';

type Row = {
  id: number;
  issuanceId: string;
  userId: string;
  configId: string;
  format: string;
  issuanceState: string;
  credentialStatus: string;
  issuedAt?: string;
};

export default function Oid4vciIssuedCredentialPage() {
  const navigate = useNavigate();
  const [rows, setRows] = useState<Row[]>([]);
  const [loading, setLoading] = useState(false);
  const [totalRows, setTotalRows] = useState(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [searchText, setSearchText] = useState('');
  const [selectedSearch, setSelectedSearch] = useState('issuanceId');
  const [statusDialogOpen, setStatusDialogOpen] = useState(false);
  const [newStatus, setNewStatus] = useState('');
  const [reason, setReason] = useState('');
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchOid4vciIssuedCredentials(
        paginationModel.page,
        paginationModel.pageSize,
        searchText.trim() ? selectedSearch : null,
        searchText.trim() || null,
      );
      setRows(response.data.content);
      setTotalRows(response.data.totalElements ?? response.data.total ?? 0);
    } catch (error) {
      navigate('/error', { state: { message: formatErrorMessage(error, 'Failed to retrieve OID4VCI credentials.') } });
    } finally {
      setLoading(false);
    }
  }, [navigate, paginationModel.page, paginationModel.pageSize, searchText, selectedSearch]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const selectedRowData = useMemo(
    () => rows.find(row => row.id === selectedRow) ?? null,
    [rows, selectedRow],
  );

  const availableStatuses = useMemo(() => {
    if (!selectedRowData || selectedRowData.issuanceState !== 'ISSUED') return [];
    switch (selectedRowData.credentialStatus) {
      case 'VALID': return ['SUSPENDED', 'INVALID'];
      case 'SUSPENDED': return ['VALID', 'INVALID'];
      default: return [];
    }
  }, [selectedRowData]);

  const openStatusDialog = () => {
    if (availableStatuses.length === 0) {
      window.alert('Only issued credentials in VALID or SUSPENDED status can be changed.');
      return;
    }
    setNewStatus(availableStatuses[0]);
    setReason('');
    setStatusDialogOpen(true);
  };

  const updateStatus = async () => {
    if (!selectedRowData || !newStatus || !reason.trim()) return;
    setLoading(true);
    try {
      await changeOid4vciCredentialStatus(selectedRowData.id, newStatus, reason.trim());
      setStatusDialogOpen(false);
      setSelectedRow(null);
      await fetchData();
    } catch (error) {
      navigate('/error', { state: { message: formatErrorMessage(error, 'Failed to change credential status.') } });
    } finally {
      setLoading(false);
    }
  };

  const Container = useMemo(() => styled(Box)(({ theme }) => ({
    margin: 'auto', marginTop: theme.spacing(1), padding: theme.spacing(3),
    borderRadius: theme.shape.borderRadius, backgroundColor: '#fff', boxShadow: '0 4px 8px #0000001A',
  })), []);

  return (
    <>
      <FullscreenLoader open={loading} />
      <Container>
        <Typography fontSize={24} fontWeight={700}>OID4VCI Credentials</Typography>
        <CustomDataGrid
          rows={rows}
          columns={[
            { field: 'issuanceId', headerName: 'Issuance ID', width: 290, renderCell: (params) => (
              <Link component="button" variant="body2" onClick={() => navigate(`/issued-credentials/oid4vci/${params.row.id}`)}>
                {params.value}
              </Link>
            ) },
            { field: 'userId', headerName: 'User ID', width: 150 },
            { field: 'configId', headerName: 'Configuration ID', width: 190 },
            { field: 'format', headerName: 'Format', width: 150 },
            { field: 'issuanceState', headerName: 'Issuance State', width: 140 },
            { field: 'credentialStatus', headerName: 'Credential Status', width: 150 },
            { field: 'issuedAt', headerName: 'Issued At', width: 190 },
          ]}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          onEdit={openStatusDialog}
          editLabel="Change Status"
          onRefresh={fetchData}
          paginationMode="server"
          totalRows={totalRows}
          paginationModel={paginationModel}
          setPaginationModel={setPaginationModel}
          enableSearch
          onSearch={(field, value) => {
            setSelectedSearch(field);
            setSearchText(value.trim());
            setPaginationModel(previous => ({ ...previous, page: 0 }));
          }}
          searchText={searchText}
          setSearchText={setSearchText}
          selectedSearch={selectedSearch}
          setSelectedSearch={setSelectedSearch}
          searchOptions={[
            { value: 'issuanceId', label: 'Issuance ID' },
            { value: 'userId', label: 'User ID' },
            { value: 'configId', label: 'Configuration ID' },
            { value: 'format', label: 'Format' },
            { value: 'issuanceState', label: 'Issuance State' },
            { value: 'credentialStatus', label: 'Credential Status' },
          ]}
          selectableFields={[
            {
              field: 'format',
              options: [
                { value: 'dc+sd-jwt', label: 'SD-JWT' },
                { value: 'mso_mdoc', label: 'mdoc' },
              ],
            },
            {
              field: 'issuanceState',
              options: [
                { value: 'ALLOCATED', label: 'Allocated' },
                { value: 'ISSUED', label: 'Issued' },
                { value: 'FAILED', label: 'Failed' },
              ],
            },
            {
              field: 'credentialStatus',
              options: [
                { value: 'VALID', label: 'Valid' },
                { value: 'SUSPENDED', label: 'Suspended' },
                { value: 'INVALID', label: 'Invalid' },
                { value: 'RESERVED', label: 'Reserved' },
              ],
            },
          ]}
        />
      </Container>
      <Dialog open={statusDialogOpen} onClose={() => setStatusDialogOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Change Credential Status</DialogTitle>
        <DialogContent>
          <FormControl fullWidth margin="normal">
            <InputLabel id="oid4vci-status-label">Status</InputLabel>
            <Select
              labelId="oid4vci-status-label"
              label="Status"
              value={newStatus}
              onChange={event => setNewStatus(event.target.value)}
            >
              {availableStatuses.map(status => (
                <MenuItem key={status} value={status}>{status}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Reason"
            value={reason}
            onChange={event => setReason(event.target.value)}
            required
            fullWidth
            multiline
            minRows={3}
            margin="normal"
            inputProps={{ maxLength: 512 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setStatusDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={updateStatus} disabled={!newStatus || !reason.trim()}>
            Change Status
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
