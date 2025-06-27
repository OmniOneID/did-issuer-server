import {
  Box, Button, IconButton, MenuItem, Paper, Select, Table, TableBody,
  TableCell, TableContainer, TableHead, TableRow, TextField, Typography,
  useTheme, FormControl, InputLabel, styled,
  Popover
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { getCredentialDefinition } from '../../../apis/zkp_management-api';
import { attributeTypes } from "../../../constants/attribute-types";
import { useDialogs } from "@toolpad/core";
import CustomDialog from "../../../components/dialog/CustomDialog";

interface FormData {
    schemaName: string;
    definitionId: string;
    definition: string;
    alias: string;
    version: string;
    type: string;
    tag: string;
    status: string;
    createdAt: string;
    updatedAt: string;
}



const CredentialDefinitionDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

  const numericSchemaId = id ? parseInt(id, 10) : null;

  const [formData, setFormData] = useState<FormData>({
    schemaName: "",
    definitionId: "",
    definition: "",
    alias: "",
    version: "",
    type: "",
    tag: "",
    status: "",
    createdAt: "",
    updatedAt: ""
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
            navigate("/zkp-management/credential-definition-management", { replace: true }),
        });
        return;
      }
      try {
        const { data } = await getCredentialDefinition(numericSchemaId);
        setFormData({
            schemaName: data.schemaName,
            definitionId: data.definitionId,
            definition: data.definition,
            alias: data.alias,
            version: data.version,
            type: data.type,
            tag: data.tag,
            status: data.status,
            createdAt: data.createdAt,
            updatedAt: data.updatedAt
        });
      } catch (err) {
        console.error("Failed to load credential definitino:", err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [id]);

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
        <Typography variant="h4">Credential Definition Management</Typography>

        <StyledContainer>
            <StyledTitle>Credential Definition Detail Information</StyledTitle>

            <StyledInputArea>
                <TextField
                    label="Schema Name"
                    fullWidth
                    size="small"
                    margin="normal"
                    variant="standard"
                    value={formData.schemaName}
                    slotProps={{ input: { readOnly: true } }}
                />

                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <TextField
                        label="Definition ID"
                        fullWidth
                        size="small"
                        margin="normal"
                        variant="standard"
                        value={formData.definitionId}
                        slotProps={{ input: { readOnly: true } }}
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
                        View Definition
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
                                const parsed = JSON.parse(formData.definition);
                                return JSON.stringify(parsed, null, 2);
                                } catch (e) {
                                return 'Invalid JSON format';
                                }
                            })()}
                        </Typography>
                    </Box>
                </Popover>
                <TextField
                    label="Alias"
                    fullWidth
                    size="small"
                    margin="normal"
                    variant="standard"
                    value={formData.alias}
                    slotProps={{ input: { readOnly: true } }}
                />
                <TextField
                    label="Definition Version"
                    fullWidth
                    size="small"
                    margin="normal"
                    variant="standard"
                    value={formData.version}
                    slotProps={{ input: { readOnly: true } }}
                />

                <TextField
                    label="Definition Type"
                    fullWidth
                    size="small"
                    margin="normal"
                    variant="standard"
                    value={formData.type}
                    slotProps={{ input: { readOnly: true } }}
                />

                <TextField
                    label="Definition Tag"
                    fullWidth
                    size="small"
                    margin="normal"
                    variant="standard"
                    value={formData.tag}
                    slotProps={{ input: { readOnly: true } }}
                />

                <TextField
                    label="Status"
                    fullWidth
                    size="small"
                    margin="normal"
                    variant="standard"
                    value={formData.status}
                    slotProps={{ input: { readOnly: true } }}
                />

                <TextField
                    label="Registered At"
                    fullWidth
                    size="small"
                    margin="normal"
                    variant="standard"
                    value={formData.createdAt}
                    slotProps={{ input: { readOnly: true } }}
                />

                {formData?.updatedAt && (
                        <TextField 
                            fullWidth 
                            label="Updated At" 
                            variant="standard" 
                            margin="normal" 
                            value={formData?.updatedAt || ''} 
                            slotProps={{ input: { readOnly: true } }} 
                        />
                )}

                <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mt: 4 }}>
                    <Button variant="outlined" onClick={() => navigate(-1)}>Back</Button>
                </Box>
            </StyledInputArea>
        </StyledContainer>
    </>
  )
}

export default CredentialDefinitionDetailPage