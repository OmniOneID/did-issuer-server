import {
  Box, Button, IconButton, MenuItem, Paper, Select, Table, TableBody,
  TableCell, TableContainer, TableHead, TableRow, TextField, Typography,
  useTheme, FormControl, InputLabel, styled,
  Popover
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { getZkpSchema } from '../../../apis/zkp_management-api';
import { attributeTypes } from "../../../constants/attribute-types";
import { useDialogs } from "@toolpad/core";
import CustomDialog from "../../../components/dialog/CustomDialog";

type ItemType = "String" | "Number";

interface Item {
  label: string;
  type: ItemType;
  caption: string;
}

interface FormData {
  schemaId: string;
  name: string;
  ref: string;
  schema: string;
  status: string;
  items: Item[];
}

const CredentialSchemaDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

  const numericSchemaId = id ? parseInt(id, 10) : null;

  const [formData, setFormData] = useState<FormData>({
    schemaId: "",
    name: "",
    ref: "",
    schema: "",
    status: "",
    items: [],
  });
  const [isLoading, setIsLoading] = useState(true);

  const handlePopoverOpen = (event: React.MouseEvent<HTMLElement>) => {
      setAnchorEl(event.currentTarget);
  };

  const handlePopoverClose = () => {
    setAnchorEl(null);
  };

  useEffect(() => {
    const fetchData = async () => {
      if (numericSchemaId === null || isNaN(numericSchemaId)) {
        await dialogs.open(CustomDialog, {
          title: "Notification",
          message: "Invalid Path.",
          isModal: true,
        }, {
          onClose: async () =>
            navigate("/zkp-management/credential-schema-management", { replace: true }),
        });
        return;
      }
      try {
        const { data } = await getZkpSchema(numericSchemaId);
        setFormData({
          schemaId: data.schemaId,
          name: data.name,
          ref: data.ref || "",
          schema: data.schema,
          status: data.status,
          items: data.attributes || [],
        });
      } catch (err) {
        console.error("Failed to load schema:", err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    width: 900,
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
      <Typography variant="h4">Credential Schema Management</Typography>

      <StyledContainer>
        <StyledTitle>Credential Schema Registration</StyledTitle>

        <StyledInputArea>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <TextField
                label="Schema ID"
                fullWidth
                size="small"
                margin="normal"
                variant="standard"
                value={formData.schemaId}
                slotProps={{ input: { readOnly: true } }}
                sx={{ width: '60%' }}
            />
            <Button 
                variant="outlined" 
                size="small" 
                onClick={handlePopoverOpen} 
                sx={{
                    height: '100%', 
                    flexShrink: 0, 
                    whiteSpace: 'nowrap', 
                    minWidth: 'auto',
                }}
            >
                View Schema
            </Button>
          </Box>
        <Popover
          open={Boolean(anchorEl)}
          anchorEl={anchorEl}
          onClose={handlePopoverClose}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
        >
          <Box sx={{ p: 2, maxWidth: 500 }}>
            <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
               {(() => {
                    try {
                    const parsed = JSON.parse(formData.schema);
                    return JSON.stringify(parsed, null, 2);
                    } catch (e) {
                    return 'Invalid JSON format';
                    }
                })()}
            </Typography>
          </Box>
        </Popover>


          <TextField
            label="Name"
            fullWidth
            size="small"
            margin="normal"
            variant="standard"
            value={formData.name}
            slotProps={{ input: { readOnly: true } }}
            sx={{ width: '60%' }}
          />

          <TextField
            label="Ref"
            fullWidth
            size="small"
            margin="normal"
            variant="standard"
            value={formData.ref}
            slotProps={{ input: { readOnly: true } }}
            sx={{ width: '60%' }}
          />

          <TextField
            label="Status"
            fullWidth
            size="small"
            margin="normal"
            variant="standard"
            value={formData.status}
            slotProps={{ input: { readOnly: true } }}
            sx={{ width: '60%' }}
          />

          <Typography variant="h6" sx={{ mt: 3 }}>Items</Typography>

          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                  <TableCell sx={{ width: 200 }}>Label</TableCell>
                  <TableCell sx={{ width: 150 }}>Type</TableCell>
                  <TableCell>Caption</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {formData.items.map((item, index) => (
                  <TableRow key={index}>
                    <TableCell>
                      <TextField
                        fullWidth
                        size="small"
                        value={item.label}
                        slotProps={{ input: { readOnly: true } }}
                      />
                    </TableCell>
                    <TableCell>
                      <TextField
                        fullWidth
                        size="small"
                        value={
                          attributeTypes.find(type => type.value === item.type)?.label || item.type
                        }
                        slotProps={{ input: { readOnly: true } }}
                      />
                    </TableCell>
                    <TableCell>
                      <TextField
                        fullWidth
                        size="small"
                        value={item.caption}
                        slotProps={{ input: { readOnly: true } }}
                      />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mt: 4 }}>
            <Button variant="outlined" onClick={() => navigate(-1)}>Back</Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );
};

export default CredentialSchemaDetailPage;
