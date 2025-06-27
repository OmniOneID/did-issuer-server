import { useDialogs } from "@toolpad/core";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getNamespace } from "../../../apis/vc-management-api";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { Box, Button, Paper, styled, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";
import { formatErrorMessage } from "../../../utils/error-handler";

type Props = {}

const NamespaceDetailPage = (props: Props) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const numericNamespaceId = id ? parseInt(id, 10) : null;
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [namespaceData, setNamespaceData] = useState<any>(null);
  const params = new URLSearchParams(window.location.search);
  const isPopup = params.get('isPopup') === 'true';


  useEffect(() => {
    const fetchData = async () => {
      if (numericNamespaceId === null || isNaN(numericNamespaceId)) {
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Invalid Path.',
          isModal: true
        }, {
          onClose: async () => navigate('/vc-management/namespace-management', { replace: true }),
        });
        return;
      }

      setIsLoading(true);

      try {
        const { data } = await getNamespace(numericNamespaceId);
        setNamespaceData({
          namespaceId: data.namespaceId,
          name: data.name,
          ref: data.ref,
          items: data.schemaClaims.items,
        });
        setIsLoading(false);
      } catch (err) {
        console.error('Failed to fetch Namespace information:', err);
        setIsLoading(false);
        navigate('/error', { state: { message: formatErrorMessage(err, "Failed to namespace information.") } });
      }
    };

    fetchData();
  }, [numericNamespaceId]);

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
      width: 800,
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
      <Typography variant="h4">Namespace Management</Typography>
      <StyledContainer>
        <StyledTitle>Namespace Detail Information</StyledTitle>

        <StyledInputArea>
          <TextField
            label="Namespace ID"
            value={namespaceData?.namespaceId || ''}
            fullWidth
            variant="standard"
            margin="normal"
            sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="Name"
            value={namespaceData?.name || ''}
            fullWidth
            variant="standard"
            margin="normal"
            sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="Ref"
            value={namespaceData?.ref || ''}
            fullWidth
            variant="standard"
            margin="normal"
            sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <Typography variant="h6" sx={{ mt: 3 }}>Items</Typography>

          <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: "auto", mt: 2 }}>
            <Table sx={{ tableLayout: "fixed", width: "100%" }}>
              <TableHead>
                <TableRow sx={{ backgroundColor: theme.palette.mode === "dark" ? theme.palette.background.paper : "#f5f5f5" }}>
                  <TableCell sx={{ width: 150 }}>ID</TableCell>
                  <TableCell sx={{ width: 100 }}>Type</TableCell>
                  <TableCell sx={{ width: 150 }}>Format</TableCell>
                  <TableCell sx={{ width: 200 }}>Caption</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {namespaceData?.items?.length > 0 ? (
                  namespaceData.items.map((item: any, index: number) => (
                    <TableRow key={index}>
                      <TableCell sx={{ width: 150 }}>{item.id}</TableCell>
                      <TableCell sx={{ width: 100 }}>{item.type}</TableCell>
                      <TableCell sx={{ width: 150 }}>{item.format.toUpperCase()}</TableCell>
                      <TableCell sx={{ width: 200 }}>{item.caption}</TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={4} align="center">
                      No items available.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            {isPopup ? (
              <Button
                variant="contained"
                sx={{ mt: 3 }}
                onClick={() => window.close()} // 팝업 닫기
              >
                Close
              </Button>
            ) :
              <>
                <Button variant="contained" color="primary" onClick={() => navigate(-1)}>Back</Button>
                {/* <Button variant="outlined" color="primary" onClick={() => navigate('/vc-management/namespace-management/namespace-edit/' + numericNamespaceId)}>Go to Edit</Button> */}
              </>
            }
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );
}

export default NamespaceDetailPage;
