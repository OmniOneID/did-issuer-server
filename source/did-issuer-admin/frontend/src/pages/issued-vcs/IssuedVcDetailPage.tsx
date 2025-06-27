import { useDialogs } from "@toolpad/core";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getIssuedVc } from "../../apis/vc-management-api";
import CustomDialog from "../../components/dialog/CustomDialog";
import FullscreenLoader from "../../components/loading/FullscreenLoader";
import { Box, Button, styled, TextField, Typography, useTheme } from "@mui/material";
import { formatErrorMessage } from "../../utils/error-handler";

type Props = {}

const IssuedVcDetailPage = (props: Props) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const numericIssuedVcId = id ? parseInt(id, 10) : null;
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [issuedVcData, setIsuedVcData] = useState<any>(null);


  useEffect(() => {
    const fetchData = async () => {
      if (numericIssuedVcId === null || isNaN(numericIssuedVcId)) {
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Invalid Path.',
          isModal: true
        }, {
          onClose: async () => navigate('/issued-vcs/issued-vc-management', { replace: true }),
        });
        return;
      }

      setIsLoading(true);

      try {
        const { data } = await getIssuedVc(numericIssuedVcId);
        setIsuedVcData({
          vcId: data.vcId,
          did: data.did,
          vcSchemaId: data.vcSchemaId,
          status: data.status,
          createdAt: data.createdAt,
          updatedAt: data.updatedAt,
        });
        setIsLoading(false);
      } catch (err) {
        console.error('Failed to fetch Namespace information:', err);
        setIsLoading(false);
        navigate('/error', { state: { message: formatErrorMessage(err, "Failed to namespace information.") } });
      }
    };

    fetchData();
  }, [numericIssuedVcId]);

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    width: 600,
    margin: 'auto',
    marginTop: theme.spacing(1),
    padding: theme.spacing(3),
    border: 'none',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: '#ffffff',
    boxShadow: '0px 4px 8px 0px #0000001A',
  })), []);

  const StyledTitle = useMemo(() => styled(Typography)({
    textAlign: 'left',
    fontSize: '24px',
    fontWeight: 700,
  }), []);

  const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
    marginTop: theme.spacing(2),
  })), []);
  
  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Typography variant="h4">Issued VC Management</Typography>
      <StyledContainer>
        <StyledTitle>Issued VC Detail Information</StyledTitle>

        <StyledInputArea>
          <TextField
            label="VC ID"
            value={issuedVcData?.vcId || ''}
            fullWidth
            variant="standard"
            margin="normal"
            // sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="DID"
            value={issuedVcData?.did || ''}
            fullWidth
            variant="standard"
            margin="normal"
            // sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="VC Schema ID"
            value={issuedVcData?.vcSchemaId || ''}
            fullWidth
            variant="standard"
            margin="normal"
            // sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="Status"
            value={issuedVcData?.status || ''}
            fullWidth
            variant="standard"
            margin="normal"
            // sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="Created At"
            value={issuedVcData?.createdAt || ''}
            fullWidth
            variant="standard"
            margin="normal"
            // sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="Updated At"
            value={issuedVcData?.updatedAt || ''}
            fullWidth
            variant="standard"
            margin="normal"
            // sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />


          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            <Button variant="contained" color="primary" onClick={() => navigate(-1)}>Back</Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );
}

export default IssuedVcDetailPage;
