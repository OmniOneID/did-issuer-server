import { useDialogs } from "@toolpad/core";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getIssueProfile } from "../../../apis/vc-management-api";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { Box, Button, Paper, styled, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";

type Props = {}

const IssueProfileDetailPage = (props: Props) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const numericIssueProfileId = id ? parseInt(id, 10) : null;
  let [numericVcSchemaId, setNumericVcSchemaId ]= useState<string>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [issueProfileData, setIssueProfileData] = useState<any>(null);
  const params = new URLSearchParams(window.location.search);
  const isPopup = params.get('isPopup') === 'true';


  // Initiate Type 옵션
  const initiateTypeOptions = [
    { key: "User Initiate", value: "user_init" },
    { key: "Issuer Initiate", value: "issuer_init" }
  ];

  // 선택한 initiateType의 key 반환 함수
  const getInitiateTypeKey = (value: string) => {
    return initiateTypeOptions.find(option => option.value === value)?.key || "Unknown";
  };

  const handleOpenVcSchemaDetail = () => {
    console.log(numericVcSchemaId)
    window.open(`/vc-management/vc-schema-management-popup/${numericVcSchemaId}?isPopup=true`, "vc schema detail", "popup=yes, width=850, height=800");
  };

  useEffect(() => {
    const fetchData = async () => {
      if (numericIssueProfileId === null || isNaN(numericIssueProfileId)) {
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Invalid Path.',
          isModal: true
        }, {
          onClose: async () => navigate('/vc-management/issue-profile-management', { replace: true }),
        });
        return;
      }

      setIsLoading(true);

      try {
        const { data } = await getIssueProfile(numericIssueProfileId);
        setIssueProfileData({
          vcPlanId: data.issueProfile.vcSchemaId,
          title: data.issueProfile.title,
          description: data.issueProfile.description,
          vcSchemaId: data.vcSchemaName,
          endpoints: data.issueProfile.endpoints,
          cipher: data.issueProfile.cipher,
          curve: data.issueProfile.curve,
          padding: data.issueProfile.padding,
          language: data.issueProfile.language,
          initiateType: data.issueProfile.initiateType,
          tags: data.issueProfile.tags,
        });
        setNumericVcSchemaId(data.issueProfile.vcSchemaId);
      } catch (err) {
        console.error('Failed to fetch Issue Profile:', err);
        navigate('/error', { state: { message: `Failed to fetch Issue Profile: ${err}` } });
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [numericIssueProfileId]);
  
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
      <Typography variant="h4">Issue Profile Management</Typography>
      <StyledContainer>
        <StyledTitle>Issue Profile Detail Information</StyledTitle>

        <StyledInputArea>
          <TextField label="VC Plan ID" value={issueProfileData?.vcSchemaId || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          <TextField label="Title" value={issueProfileData?.title || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          <TextField label="Description" value={issueProfileData?.description || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          <Box onClick={handleOpenVcSchemaDetail}>
            <TextField label="VC Schema ID" value={issueProfileData?.vcSchemaId || ''} fullWidth variant="standard" margin="normal" sx={{ input: {color: "blue", textDecoration: "underline", cursor: "pointer"  } }} slotProps={{ input: { readOnly: true } }} />
          </Box>
          
          <TextField
            label="Initiate Type"
            value={getInitiateTypeKey(issueProfileData?.initiateType)}
            fullWidth
            variant="standard"
            margin="normal"
            slotProps={{ input: { readOnly: true } }}
          />
          <TextField label="Language" value={issueProfileData?.language || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />

          <Typography variant="h6" sx={{ mt: 3 }}>Endpoints</Typography>
          <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: "auto", mt: 2 }}>
            <Table>

              <TableBody>
                {issueProfileData?.endpoints?.length > 0 ? (
                  issueProfileData.endpoints.map((item: any, index: number) => (
                    <TableRow key={index}>
                      <TableCell>{item}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell align="center">No items available.</TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>

          <Typography variant="h6" sx={{ mt: 3 }}>E2E</Typography>
          <TextField label="Cipher" value={issueProfileData?.cipher || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          <TextField label="Curve" value={issueProfileData?.curve || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />
          <TextField label="Padding" value={issueProfileData?.padding || ''} fullWidth variant="standard" margin="normal" slotProps={{ input: { readOnly: true } }} />

          <Typography variant="h6" sx={{ mt: 3 }}>Tags</Typography>
          <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: "auto", mt: 2 }}>
            <Table>
              <TableBody>
                {issueProfileData?.tags?.length > 0 ? (
                  issueProfileData.tags.map((item: any, index: number) => (
                    <TableRow key={index}>
                      <TableCell>{item}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell align="center">No items available.</TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>

          <Typography variant="h6" sx={{ mt: 3 }}></Typography>

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            {isPopup ? (
              <Button variant="contained" sx={{ mt: 3 }} onClick={() => window.close()}>Close</Button>
            ) : (
              <>
                <Button variant="outlined" color="secondary" onClick={() => navigate(-1)}>Back</Button>
                <Button variant="outlined" color="secondary" onClick={() => navigate(`/vc-management/issue-profile-management/issue-profile-edit/${numericIssueProfileId}`)}>Go to Edit</Button>
              </>
            )}
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );
}

export default IssueProfileDetailPage;
