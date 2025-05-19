import { useDialogs } from "@toolpad/core";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { getZkpNamespace } from "../../../apis/zkp_management-api";
import CustomDialog from "../../../components/dialog/CustomDialog";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import {
  Box,
  Button,
  Paper,
  styled,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
  useTheme,
} from "@mui/material";
import { formatErrorMessage } from "../../../utils/error-handler";

const ZkpNamespaceDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const numericNamespaceId = id ? parseInt(id, 10) : null;
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [namespace, setNamespace] = useState<any>(null);
  const [attributes, setAttributes] = useState<any[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      if (numericNamespaceId === null || isNaN(numericNamespaceId)) {
        await dialogs.open(CustomDialog, {
          title: "Notification",
          message: "Invalid Path.",
          isModal: true,
        }, {
          onClose: async () =>
            navigate("/zkp-management/zkp-namespace-management", { replace: true }),
        });
        return;
      }

      setIsLoading(true);
      try {
        const { data } = await getZkpNamespace(numericNamespaceId);

        setNamespace(data.namespace);       // ZkpNamespaceSaveDto
        setAttributes(data.attributes || []); // List<ZkpAttributeSaveDto>
        setIsLoading(false);
      } catch (err) {
        console.error("Failed to fetch ZKP Namespace:", err);
        setIsLoading(false);
        navigate("/error", {
          state: { message: formatErrorMessage(err, "Failed to load namespace information.") },
        });
      }
    };

    fetchData();
  }, [numericNamespaceId]);

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    width: 800,
    margin: "auto",
    marginTop: theme.spacing(1),
    padding: theme.spacing(3),
    border: "none",
    borderRadius: theme.shape.borderRadius,
    backgroundColor: "#ffffff",
    boxShadow: "0px 4px 8px 0px #0000001A",
  })), []);

  const StyledTitle = useMemo(() => styled(Typography)({
    textAlign: "left",
    fontSize: "24px",
    fontWeight: 700,
  }), []);

  const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
    marginTop: theme.spacing(2),
  })), []);

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Typography variant="h4">ZKP Namespace Management</Typography>
      <StyledContainer>
        <StyledTitle>ZKP Namespace Detail Information</StyledTitle>

        <StyledInputArea>
          <TextField
            label="Namespace ID"
            fullWidth
            size="small"
            margin="normal"
            sx={{ width: "60%" }}
            value={namespace?.namespaceId || ""}
            slotProps={{ input: { readOnly: true } }}
            variant="standard"
          />

          <TextField
            label="Name"
            fullWidth
            size="small"
            margin="normal"
            sx={{ width: "60%" }}
            value={namespace?.name || ""}
            slotProps={{ input: { readOnly: true } }}
            variant="standard"
          />

          <TextField
            label="Ref"
            fullWidth
            size="small"
            margin="normal"
            sx={{ width: "60%" }}
            value={namespace?.ref || ""}
            slotProps={{ input: { readOnly: true } }}
            variant="standard"
          />

          <Typography variant="h6" sx={{ mt: 3 }}>
            Attributes
          </Typography>

          <TableContainer component={Paper} sx={{ mt: 1 }}>
            <Table>
              <TableHead>
                <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                  <TableCell sx={{ width: 200 }}>Label</TableCell>
                  <TableCell sx={{ width: 150 }}>Type</TableCell>
                  <TableCell>Caption</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {attributes.map((item, index) => (
                  <TableRow key={index}>
                    <TableCell>{item.label}</TableCell>
                    <TableCell>{item.type}</TableCell>
                    <TableCell>{item.caption}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mt: 4 }}>
           <Button variant="contained" color="primary" onClick={() => navigate(-1)}>Back</Button>
           <Button variant="outlined" color="primary" onClick={() => navigate('/zkp-management/zkp-namespace-management/zkp-namespace-edit/' + numericNamespaceId)}>Go to Edit</Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );
};

export default ZkpNamespaceDetailPage;
