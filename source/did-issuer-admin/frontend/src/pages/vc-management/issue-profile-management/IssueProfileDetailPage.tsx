import { useDialogs } from "@toolpad/core";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getIssueProfile } from "../../../apis/vc-management-api";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { Box, Button, Paper, Table, TableBody, TableCell, TableContainer, TableRow, TextField, Typography, useTheme } from "@mui/material";

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
  const isPopup = !!window.opener;

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
    window.open(`/vc-management/vc-schema-management-popup/${numericVcSchemaId}`, "vc schema detail", "popup=yes, width=800, height=650");
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
          initiateType: data.issueProfile.initiateType
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

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Box sx={{ p: 3 }}>
        <Typography variant="h4">Issue Profile Detail Information</Typography>

        <Box sx={{ maxWidth: 800, margin: 'auto', mt: 2, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
          <TextField label="VC Plan ID" value={issueProfileData?.vcSchemaId || ''} fullWidth variant="standard" margin="normal" sx={{ width: '60%' }} slotProps={{ input: { readOnly: true } }} />
          <TextField label="Title" value={issueProfileData?.title || ''} fullWidth variant="standard" margin="normal" sx={{ width: '60%' }} slotProps={{ input: { readOnly: true } }} />
          <TextField label="Description" value={issueProfileData?.description || ''} fullWidth variant="standard" margin="normal" sx={{ width: '60%' }} slotProps={{ input: { readOnly: true } }} />
          <Box onClick={handleOpenVcSchemaDetail}>
            <TextField label="VC Schema ID" value={issueProfileData?.vcSchemaId || ''} fullWidth variant="standard" margin="normal" sx={{ width: '60%', input: {color: "blue", textDecoration: "underline", cursor: "pointer"  } }} slotProps={{ input: { readOnly: true } }} />
          </Box>
          
          

          <TextField
            label="Initiate Type"
            value={getInitiateTypeKey(issueProfileData?.initiateType)}
            fullWidth
            variant="standard"
            margin="normal"
            sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />
          <TextField label="Language" value={issueProfileData?.language || ''} fullWidth variant="standard" margin="normal" sx={{ width: '60%' }} slotProps={{ input: { readOnly: true } }} />


          <Typography variant="h6" sx={{ mt: 3 }}>Endpoints</Typography>
          <TableContainer component={Paper} sx={{ maxHeight: 400, width: '60%', overflow: "auto", mt: 2 }}>
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
          <TextField label="Cipher" value={issueProfileData?.cipher || ''} fullWidth variant="standard" margin="normal" sx={{ width: '60%' }} slotProps={{ input: { readOnly: true } }} />
          <TextField label="Curve" value={issueProfileData?.curve || ''} fullWidth variant="standard" margin="normal" sx={{ width: '60%' }} slotProps={{ input: { readOnly: true } }} />
          <TextField label="Padding" value={issueProfileData?.padding || ''} fullWidth variant="standard" margin="normal" sx={{ width: '60%' }} slotProps={{ input: { readOnly: true } }} />


          <Typography variant="h6" sx={{ mt: 3 }}></Typography>

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            {isPopup ? (
              <Button variant="contained" sx={{ mt: 3 }} onClick={() => window.close()}>Close</Button>
            ) : (
              <>
                <Button variant="contained" color="secondary" onClick={() => navigate('/vc-management/issue-profile-management')}>Back</Button>
                <Button variant="contained" color="primary" onClick={() => navigate(`/vc-management/issue-profile-management/issue-profile-edit/${numericIssueProfileId}`)}>Edit</Button>
              </>
            )}
          </Box>
        </Box>
      </Box>
    </>
  );
}

export default IssueProfileDetailPage;
