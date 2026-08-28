import { Box, Button, Paper, TextField, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { getOid4vciIssuedCredential } from '../../apis/oid4vci-issued-credentials-api';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../utils/error-handler';

export default function Oid4vciIssuedCredentialDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [data, setData] = useState<Record<string, unknown> | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const numericId = Number(id);
    if (!Number.isInteger(numericId)) {
      navigate('/issued-credentials/oid4vci', { replace: true });
      return;
    }
    setLoading(true);
    getOid4vciIssuedCredential(numericId)
      .then(response => setData(response.data))
      .catch(error => navigate('/error', { state: { message: formatErrorMessage(error, 'Failed to retrieve OID4VCI credential.') } }))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const fields = [
    ['issuanceId', 'Issuance ID'], ['userId', 'User ID'], ['configId', 'Configuration ID'],
    ['format', 'Format'], ['issuanceState', 'Issuance State'], ['credentialStatus', 'Credential Status'],
    ['issuedAt', 'Issued At'], ['expiresAt', 'Expires At'], ['statusChangedAt', 'Status Changed At'],
    ['statusListUri', 'Status List URI'], ['statusListIndex', 'Status List Index'], ['tokenHash', 'Token Hash'],
    ['failedAt', 'Failed At'], ['failureCode', 'Failure Code'], ['failureMessage', 'Failure Message'],
  ];

  return (
    <>
      <FullscreenLoader open={loading} />
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" sx={{ mb: 2 }}>OID4VCI Credential</Typography>
        <Paper sx={{ p: 3 }}>
          {fields.map(([key, label]) => (
            <TextField key={key} label={label} value={data?.[key] ?? ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          ))}
          <Button sx={{ mt: 3 }} onClick={() => navigate('/issued-credentials/oid4vci')}>Back</Button>
        </Paper>
      </Box>
    </>
  );
}
