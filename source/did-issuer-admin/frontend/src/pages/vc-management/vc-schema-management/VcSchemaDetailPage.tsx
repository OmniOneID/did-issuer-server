import { useDialogs } from "@toolpad/core";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getVcSchema } from "../../../apis/vc-management-api";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { Box, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";

type Props = {}

const VcSchemaDetailPage = (props: Props) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const numericVcSchemaId = id ? parseInt(id, 10) : null;
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [vcSchemaData, setVcSchemaData] = useState<any>(null);
  const isPopup = !!window.opener;

  // namespaceId 클릭 시 상세 정보 페이지 새창 열기
  const handleOpenNamespaceDetail = (namespaceId: string) => {
    window.open(`/vc-management/namespace-management-popup/${namespaceId}`, "namespace detail", "popup=yes, width=800, height=900");
  };

  useEffect(() => {
    const fetchData = async () => {
      if (numericVcSchemaId === null || isNaN(numericVcSchemaId)) {
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Invalid Path.',
          isModal: true
        }, {
          onClose: async () => navigate('/vc-management/vc-schema-management', { replace: true }),
        });
        return;
      }

      setIsLoading(true);

      try {
        const { data } = await getVcSchema(numericVcSchemaId);

        setVcSchemaData({
          vcSchemaId: data.vcSchema.vcSchemaId,
          title: data.vcSchema.title,
          description: data.vcSchema.description,
          items: data.items,
          language: data.vcSchema.language,
          version: data.vcSchema.version
        });
        
        setIsLoading(false);
      } catch (err) {
        console.error('Failed to fetch Namespace information:', err);
        setIsLoading(false);
        navigate('/error', { state: { message: `Failed to namespace information: ${err}` } });
      }
    };

    fetchData();
  }, [numericVcSchemaId]);

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Box sx={{ p: 3 }}>
        <Typography variant="h4">VC Schema Detail Information</Typography>

        <Box sx={{ maxWidth: 800, margin: 'auto', mt: 2, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
          <TextField
            label="VC Schema ID"
            value={vcSchemaData?.vcSchemaId || ''}
            fullWidth
            variant="standard"
            margin="normal"
            sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="Title"
            value={vcSchemaData?.title || ''}
            fullWidth
            variant="standard"
            margin="normal"
            sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="Description"
            value={vcSchemaData?.description || ''}
            fullWidth
            variant="standard"
            margin="normal"
            sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="Language"
            value={vcSchemaData?.language || ''}
            fullWidth
            variant="standard"
            margin="normal"
            sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            label="Version"
            value={vcSchemaData?.version || ''}
            fullWidth
            variant="standard"
            margin="normal"
            sx={{ width: '60%' }}
            slotProps={{ input: { readOnly: true } }}
          />

          <Typography variant="h6" sx={{ mt: 3 }}>Credential Subject</Typography>
          <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: "auto", mt: 2 }}>
            <Table sx={{ tableLayout: "fixed", width: "100%" }}>
              <TableHead>
                <TableRow sx={{ backgroundColor: theme.palette.mode === "dark" ? theme.palette.background.paper : "#f5f5f5" }}>
                  <TableCell sx={{ width: 100 }}>ID</TableCell>
                  <TableCell sx={{ width: 150 }}>Namespace ID</TableCell>
                  <TableCell sx={{ width: 100 }}>Name</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {vcSchemaData?.items?.length > 0 ? (
                  vcSchemaData.items.map((item: any, index: number) => (
                    <TableRow key={index}>
                      <TableCell>{item.id}</TableCell>
                      <TableCell
                        sx={{ color: "blue", width: 150, textDecoration: "underline", cursor: "pointer" }}
                        onClick={(e) => {
                          handleOpenNamespaceDetail(item.id);
                        }}
                      >
                        {item.namespaceId}
                      </TableCell>
                      <TableCell sx={{ width: 100 }}>{item.name}</TableCell>
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
                <Button variant="contained" color="secondary" onClick={() => navigate('/vc-management/vc-schema-management')}>
                  Back
                </Button>
                <Button variant="contained" color="primary" onClick={() => navigate('/vc-management/vc-schema-management/vc-schema-edit/' + numericVcSchemaId)}>
                  Edit
                </Button>
              </>
            }
          </Box>
        </Box>
      </Box>
    </>
  );
}

export default VcSchemaDetailPage;
