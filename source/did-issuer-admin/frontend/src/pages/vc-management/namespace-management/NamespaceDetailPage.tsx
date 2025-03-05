import { useDialogs } from "@toolpad/core";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getNamespace } from "../../../apis/vc-management-api";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { Box, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";

type Props = {}

const NamespaceDetailPage = (props: Props) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const numericNamespaceId = id ? parseInt(id, 10) : null;
  const [isLoading, setIsLoading] = useState<boolean>(true); 
  const [namespaceData, setNamespaceData] = useState<any>(null); 

  useEffect(() => {
    const fetchData = async () => {
      if (numericNamespaceId === null || isNaN(numericNamespaceId)) {
        await dialogs.open(CustomDialog, { 
            title: 'Notification', 
            message: 'Invalid Path.', 
            isModal: true 
        },{
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
          navigate('/error', { state: { message: `Failed to namespace information: ${err}` } });
      }
    };

    fetchData();
  }, [numericNamespaceId]);

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Box sx={{ p: 3 }}>
        <Typography variant="h4">Namespace Detail Information</Typography>

        <Box sx={{ maxWidth: 800, margin: 'auto', mt: 2, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
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
            <Button variant="contained" color="secondary" onClick={() => navigate('/vc-management/namespace-management')}>
              Back
            </Button>
            <Button variant="contained" color="primary" onClick={() => navigate('/vc-management/namespace-management/namespace-edit/' + numericNamespaceId)}>
              Edit
            </Button>
          </Box>
        </Box>
      </Box>
    </>
  );
}

export default NamespaceDetailPage;
