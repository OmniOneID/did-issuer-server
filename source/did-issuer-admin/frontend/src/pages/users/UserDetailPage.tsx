import { useDialogs } from "@toolpad/core";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getIssueProfile } from "../../apis/vc-management-api";
import CustomDialog from "../../components/dialog/CustomDialog";
import FullscreenLoader from "../../components/loading/FullscreenLoader";
import { Box, Button, Paper, styled, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";
import { getUserInfo } from "../../apis/admin-api";

type Props = {}

const UserDetailPage = (props: Props) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const numerirUserId = id ? parseInt(id, 10) : null;
  let [numericVcSchemaId, setNumericVcSchemaId] = useState<string>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [userInfo, setUserInfo] = useState<any>(null);



  const handleOpenVcSchemaDetail = () => {
    console.log(numericVcSchemaId)
    window.open(`/vc-management/vc-schema-management-popup/${numericVcSchemaId}?isPopup=true`, "vc schema detail", "popup=yes, width=850, height=800");
  };

  useEffect(() => {
    const fetchData = async () => {
      if (numerirUserId === null || isNaN(numerirUserId)) {
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Invalid Path.',
          isModal: true
        }, {
          onClose: async () => navigate('/users/user-management', { replace: true }),
        });
        return;
      }

      setIsLoading(true);

      try {
        const { data } = await getUserInfo(numerirUserId);
        setUserInfo({
          did: data.did,
          vcSchemaName: data.vcSchemaName,
          data: data.data,
          pii: data.pii,
          createdAt: data.createdAt,
          updatedAt: data.updatedAt,
        });

        setNumericVcSchemaId(data.vcSchemaId);
      } catch (err) {
        console.error('Failed to fetch User Info:', err);
        navigate('/error', { state: { message: `Failed to fetch UserInfo: ${err}` } });
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [numerirUserId]);

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
      <Typography variant="h4">User Management</Typography>
      <StyledContainer>
        <StyledTitle>User Detail Information</StyledTitle>

        <StyledInputArea>
          <TextField label="DID" value={userInfo?.did || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          <Box onClick={handleOpenVcSchemaDetail}>
            <TextField label="VC Schema Name" value={userInfo?.vcSchemaName || ''} fullWidth variant="standard" margin="normal" sx={{ input: { color: "blue", textDecoration: "underline", cursor: "pointer" } }} slotProps={{ input: { readOnly: true } }} />
          </Box>
          <TextField label="PII" value={userInfo?.pii || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          <TextField label="User VC Info" size="medium" multiline value={userInfo?.data|| ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          <TextField label="Created At" value={userInfo?.createdAt || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          <TextField label="Updated At" value={userInfo?.updatedAt || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            <Button variant="outlined" color="secondary" onClick={() => navigate(-1)}>Back</Button>
            <Button variant="outlined" color="secondary" onClick={() => navigate(`/users/user-management/user-edit/${numerirUserId}`)}>Go to Edit</Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );
}

export default UserDetailPage;
