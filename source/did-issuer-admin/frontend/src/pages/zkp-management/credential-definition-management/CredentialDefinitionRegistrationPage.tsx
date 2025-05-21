import {
  Box, Button, FormControl, FormHelperText, IconButton, InputLabel, MenuItem, Paper, Select, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, TextField, Typography, styled,
  useTheme
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router";
import DeleteIcon from "@mui/icons-material/Delete";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import CustomDialog from "../../../components/dialog/CustomDialog";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import { postCredentialDefinition, getZkpSchemaAll, verifyCredentialDefinitionAliasUnique } from "../../../apis/zkp_management-api";
import { useDialogs } from "@toolpad/core";
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  useSortable,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { formatErrorMessage } from "../../../utils/error-handler";

interface FormData {
  schemaId: string;
  schemaName: string;
  alias: string;
  version: string;
  type: string;
  tag: string;
}

interface ErrorState {
  schemaName?: string;
  alias?: string;
  version?: string;
  type?: string;
  tag?: string;
}

interface ZkpSchemaData {
  id: number;
  schemaId: string;
  name: string;
  tag: string;
  version: string;
  definitionCount: number;
  status: string;
  createdAt: string;
  updatedAt?: string;
}

const CredentialDefinitionRegistrationPage = () => {
  const theme = useTheme();
  const dialogs = useDialogs();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState<ErrorState>({});
  const [schemaList, setSchemaList] = useState<ZkpSchemaData[]>([]);
  const [isAliasIsValid, setIsAliasValid] = useState(false);

  const [formData, setFormData] = useState<FormData>({
    schemaId: '',
    schemaName: '',
    alias: '',
    version: '',
    type: '',
    tag: '',
  });

  const handleReset = () => {
    setErrors({});
    setFormData({
      schemaId: "",
      schemaName: "",
      alias: "",
      version: "",
      type: "",
      tag: "",
    });
    setIsAliasValid(false);
  };

  const handleSchemaChange = (event: any) => {
    const selectedId = event.target.value;
    const selectedSchema = schemaList.find((s: any) => s.schemaId === selectedId);

    setFormData((prev) => ({
      ...prev,
      schemaId: selectedId,
      schemaName: selectedSchema?.name || '',
    }));
  };

  const handleChange = (field: keyof FormData) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData((prev) => ({ ...prev, [field]: e.target.value }));
    if (field === "alias") {
      setIsAliasValid(false);
      setErrors((prev) => ({ ...prev, alias: undefined }));
    }
  };

  const validate = () => {
    const tempErrors: ErrorState = {};

    if (!formData.schemaId.trim()) {
      tempErrors.schemaName = "Please select a Credential Schema.";
    }

    if (!formData.alias.trim()) {
      tempErrors.alias = "Please enter a Definition Alias.";
    } else if (formData.alias.length < 3 || formData.alias.length > 20) {
      tempErrors.alias = "Alias must be between 3 and 20 characters.";
    } else if (!isAliasIsValid) {
      tempErrors.alias = "Please check alias duplication.";
    }

    if (!formData.version.trim()) {
      tempErrors.version = "Please enter a Definition Version.";
    } else if (formData.version.length < 1 || formData.version.length > 10) {
      tempErrors.version = "Version must be between 1 and 10 characters.";
    }

    if (!formData.type.trim()) {
      tempErrors.type = "Please enter a Definition Type.";
    } else if (formData.type.length < 1 || formData.type.length > 32) {
      tempErrors.type = "Type must be between 1 and 32 characters.";
    }

    if (!formData.tag.trim()) {
      tempErrors.tag = "Please enter a Definition Tag.";
    } else if (formData.tag.length < 4 || formData.tag.length > 64) {
      tempErrors.tag = "Tag must be between 4 and 64 characters.";
    }

    setErrors(tempErrors);

    const isValid = Object.values(tempErrors).every((v) => !v);
    return isValid;
  };

  
  const handleSubmit = async () => {
    if (!validate()) return;

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register ZKP Credential Definition?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      try {
        await postCredentialDefinition(formData);
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Completed register ZKP Credential Definition',
          isModal: true,
        }, {
          onClose: async () => navigate('/zkp-management/credential-definition-management'),
        });
      } catch (error) {
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: formatErrorMessage(error, "Failed to register ZKP Credential Definition."),
          isModal: true,
        });
      }
    }
  };

  const handleCheckDuplicateDefinitionAlias = () => {
    verifyCredentialDefinitionAliasUnique(formData.alias)
    .then((response) => {
        if (response.data.unique === false) {
            setErrors((prev) => ({ ...prev, alias: 'Alias already exists.' }));
            setIsAliasValid(false);
        } else {        
            setIsAliasValid(true);
            setErrors((prev) => ({ ...prev, alias: undefined }));
        }
    });
  };

  useEffect(() => {
    const fetchSchemasForSelectBox = async () => {
      setIsLoading(true);
      try {
        const response = await getZkpSchemaAll();
        setSchemaList(response.data);
      } catch (err) {
        dialogs.open(CustomDialog, {
          title: 'Notification',
          message: formatErrorMessage(err, "Failed to fetch zkp schema list."),
          isModal: true,
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchSchemasForSelectBox();
  }, []);

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    width: 500,
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
        <StyledTitle>Credential Definition Registration</StyledTitle>
        <StyledInputArea>
          <FormControl fullWidth margin="normal" error={!!errors.schemaName} size="small">
            <InputLabel>Credential Schema *</InputLabel>
            <Select
              value={formData.schemaId}
              label="Credential Schema"
              onChange={handleSchemaChange}
            >
              {schemaList.map((schema: any) => (
                <MenuItem key={schema.schemaId} value={schema.schemaId}>
                  {schema.name}
                </MenuItem>
              ))}
            </Select>
            {errors.schemaName && <FormHelperText>{errors.schemaName}</FormHelperText>}
          </FormControl>
        

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <TextField
              label="Definition Alias *"
              fullWidth
              size="small"
              margin="normal"
              value={formData.alias}
              onChange={handleChange("alias")}
              error={!!errors.alias}
              helperText={errors.alias}
            />
            <Button 
                variant="contained" 
                onClick={handleCheckDuplicateDefinitionAlias}
                disabled={!formData.alias}
                sx={{ 
                    minWidth: 150,  
                    whiteSpace: 'nowrap', 
                    textTransform: 'none' 
                }}
            >
                Check Availability
            </Button>
          </Box>
        
          <TextField
            label="Definition Version *"
            fullWidth
            size="small"
            margin="normal"
            value={formData.version}
            onChange={handleChange("version")}
            error={!!errors.version}
            helperText={errors.version}
          />

          <FormControl fullWidth margin="normal" error={!!errors.type} size="small">
            <InputLabel>Definition Type *</InputLabel>
            <Select
              value={formData.type}
              label="Definition Type"
              onChange={(e) =>
                setFormData((prev) => ({ ...prev, type: e.target.value }))
              }
            >
              <MenuItem value="CL">CL</MenuItem>
            </Select>
            {errors.type && <FormHelperText>{errors.type}</FormHelperText>}
          </FormControl>

          <TextField
            label="Definition Tag *"
            fullWidth
            size="small"
            margin="normal"
            value={formData.tag}
            onChange={handleChange("tag")}
            error={!!errors.tag}
            helperText={errors.tag}
          />

          <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mt: 4 }}>
            <Button variant="contained" color="primary" onClick={handleSubmit}>Register</Button>
            <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
            <Button variant="outlined" onClick={() => navigate(-1)}>Cancel</Button>
          </Box>

        </StyledInputArea>
      </StyledContainer>
    </>
  )
}

export default CredentialDefinitionRegistrationPage 