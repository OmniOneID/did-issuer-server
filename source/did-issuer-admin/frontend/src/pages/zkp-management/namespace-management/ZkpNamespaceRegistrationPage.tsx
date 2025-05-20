import {
  Box, Button, IconButton, MenuItem, Paper, Select, SelectChangeEvent,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  TextField, Typography, useTheme, FormControl, InputLabel, styled
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import DeleteIcon from "@mui/icons-material/Delete";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { postNamespace, verifyNamespaceIdUnique } from '../../../apis/zkp_management-api';
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import CustomDialog from "../../../components/dialog/CustomDialog";
import { useDialogs } from "@toolpad/core";
import { attributeTypes } from "../../../constants/attribute-types";
import { urlRegex, ipRegex } from "../../../utils/regex";

type ItemType = "String" | "Number";

interface Item {
  label: string;
  type: ItemType;
  caption: string;
}

interface FormData {
  namespaceId: string;
  name: string;
  ref: string;
  items: Item[];
}

interface ItemError {
  label?: string;
  type?: string;
  caption?: string;
}

interface ErrorState {
  namespaceId?: string;
  name?: string;
  ref?: string;
  items?: ItemError[];
  errorItemsMessage?: string;
}

const ZkpNamespaceRegistrationPage = () => {
  const theme = useTheme();
  const dialogs = useDialogs();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<FormData>({
    namespaceId: "",
    name: "",
    ref: "",
    items: [],
  });

  const [errors, setErrors] = useState<ErrorState>({});
  const [isLoading, setIsLoading] = useState(false);
  const [isNamespaceIdIsValid, setIsNamespaceIdValid] = useState(false);

  const handleChange = (field: keyof FormData) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData((prev) => ({ ...prev, [field]: e.target.value }));
    if (field === "namespaceId") {
      setIsNamespaceIdValid(false);
      setErrors((prev) => ({ ...prev, namespaceId: undefined }));
    }
  };

  const handleItemTextChange = (index: number, field: keyof Item) =>
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const newItems = [...formData.items];
       if (field === "type") {
      newItems[index][field] = e.target.value as ItemType;
      } else {
        newItems[index][field] = e.target.value;
      }
      setFormData(prev => ({ ...prev, items: newItems }));
  };

  const handleItemSelectChange = (index: number) =>
    (e: SelectChangeEvent<string>) => {
      const newItems = [...formData.items];
      newItems[index].type = e.target.value as ItemType;
      setFormData(prev => ({ ...prev, items: newItems }));
  };

  const handleAddItem = () => {
    setFormData((prev) => ({
      ...prev,
      items: [...prev.items, {
        label: "",
        type: attributeTypes[0].value as ItemType, 
        caption: ""
      }]
    }));
  };

  const handleRemoveItem = (index: number) => {
    const newItems = [...formData.items];
    newItems.splice(index, 1);
    setFormData((prev) => ({ ...prev, items: newItems }));
  };

  const validate = () => {
    const tempErrors: ErrorState = {};

    if (!formData.namespaceId.trim()) {
      tempErrors.namespaceId = "Please enter a Namespace ID.";
    } else if (formData.namespaceId.length < 8 || formData.namespaceId.length > 64) {
      tempErrors.namespaceId = "Namespace ID must be between 8 and 64 characters.";
    } else if (!isNamespaceIdIsValid) {
      tempErrors.namespaceId = "Namespace ID is not available.";
    }

    if (!formData.name.trim()) {
      tempErrors.name = "Please enter a Name.";
    } else if (formData.name.length < 2 || formData.name.length > 64) {
      tempErrors.name = "Name must be between 2 and 64 characters.";
    }

    if (formData.ref.trim()) {
      if (formData.ref.length < 4 || formData.ref.length > 64) {
        tempErrors.ref = "Ref must be between 4 and 64 characters.";
      }
      if (!urlRegex.test(formData.ref) && !ipRegex.test(formData.ref)) {
        tempErrors.ref = "Please enter a valid URL.";
      }
    } else {
      tempErrors.ref = undefined;
    }

    if (formData.items.length === 0) {
      tempErrors.errorItemsMessage = "At least one item is required.";
    } else {
      const labelCounts = formData.items.reduce((acc, item) => {
        const labelKey = item.label.trim();
          if (labelKey) {
            acc[labelKey] = (acc[labelKey] || 0) + 1;
          }
          return acc;
      }, {} as Record<string, number>);

      tempErrors.items = formData.items.map((item) => {
        const itemErrors: ItemError = {};

        if (!item.label.trim()) {
          itemErrors.label = "Label is required.";
        } else if (labelCounts[item.label.trim()] > 1) {
          itemErrors.label = "Label must be unique.";
        }

        if (!item.type) {
          itemErrors.type = "Type is required.";
        }

        if (!item.caption.trim()) {
          itemErrors.caption = "Caption is required.";
        }

        return itemErrors;
      });
    }

    setErrors(tempErrors);

    const isValid =
      !tempErrors.namespaceId &&
      !tempErrors.name &&
      !tempErrors.ref &&
      !tempErrors.errorItemsMessage &&
      (tempErrors.items ?? []).every((e) => Object.values(e).every((v) => !v));

    return isValid;
  };

  useEffect(() => {
    const isModified = Object.values(formData).some((value) => value !== '');
  }, [formData]);

  const handleSubmit = async () => {
    if (!validate()) return;

    const namespace: any = {
      namespaceId: formData.namespaceId,
      name: formData.name,
    };

    if (formData.ref.trim() !== "") {
      namespace.ref = formData.ref;
    }

    const requestBody = {
      namespace,
      attributes: formData.items.map(item => ({
        label: item.label,
        type: item.type,
        caption: item.caption,
      })),
    };
    
    console.log("Request Body:", requestBody);

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register ZKP Namespace?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      try {
        await postNamespace(requestBody);
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Completed register ZKP namespace.',
          isModal: true,
        }, {
          onClose: async () => navigate('/zkp-management/zkp-namespace-management'),
        });
      } catch (error) {
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: `Failed to register ZKP namespace: ${error}`,
          isModal: true,
        });
      }
    }
  };

  const handleReset = () => {
    setErrors({});
    setFormData({
      namespaceId: "",
      name: "",
      ref: "",
      items: [],
    });
    setIsNamespaceIdValid(false);
  };

  const handleCheckDuplicateNamespaceId = () => {
    verifyNamespaceIdUnique(formData.namespaceId) 
    .then((response) => {
        if (response.data.unique === false) {
            setErrors((prev) => ({ ...prev, namespaceId: 'Namespace ID already exists.' }));
            setIsNamespaceIdValid(false);
        } else {        
            setIsNamespaceIdValid(true);
            setErrors((prev) => ({ ...prev, namespaceId: undefined }));
        }
    });
  };

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
      <Typography variant="h4">ZKP Namespace Management</Typography>

      <StyledContainer>
        <StyledTitle>ZKP Namespace Registration</StyledTitle>

        <StyledInputArea>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <TextField
              label="Namespace ID *"
              fullWidth
              size="small"
              margin="normal"
              sx={{ width: '60%' }}
              value={formData.namespaceId}
              onChange={handleChange("namespaceId")}
              error={!!errors.namespaceId}
              helperText={errors.namespaceId}
            />
            <Button 
                variant="contained" 
                onClick={handleCheckDuplicateNamespaceId}
                disabled={!formData.namespaceId}
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
            label="Name *"
            fullWidth
            size="small"
            margin="normal"
            sx={{ width: '60%' }}
            value={formData.name}
            onChange={handleChange("name")}
            error={!!errors.name}
            helperText={errors.name}
          />

          <TextField
            label="Ref"
            fullWidth
            size="small"
            margin="normal"
            sx={{ width: '60%' }}
            value={formData.ref}
            onChange={handleChange("ref")}
            error={!!errors.ref}
            helperText={errors.ref}
          />

          <Typography variant="h6" sx={{ mt: 3 }}>Items *</Typography>

          {errors.errorItemsMessage && (
            <Typography color="error" variant="caption" sx={{ mt: 1, display: "block" }}>
              {errors.errorItemsMessage}
            </Typography>
          )}

          <Button variant="contained" startIcon={<AddCircleOutlineIcon />} sx={{ my: 2 }} onClick={handleAddItem}>
            Add Item
          </Button>

          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                  <TableCell sx={{ width: 200 }}>Label *</TableCell>
                  <TableCell sx={{ width: 150 }}>Type *</TableCell>
                  <TableCell>Caption *</TableCell>
                  <TableCell sx={{ width: 100 }}>Delete</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {formData.items.map((item, index) => (
                  <TableRow key={index}>
                    <TableCell sx={{verticalAlign: 'top'}}>
                      <TextField
                        fullWidth
                        size="small"
                        value={item.label}
                        onChange={handleItemTextChange(index, "label")}
                        error={!!errors.items?.[index]?.label}
                        helperText={errors.items?.[index]?.label}
                      />
                    </TableCell>
                    <TableCell sx={{verticalAlign: 'top'}}>
                      <FormControl fullWidth size="small" error={!!errors.items?.[index]?.type}>
                        <Select
                          value={item.type}
                          onChange={handleItemSelectChange(index)}
                        >
                        {attributeTypes.map((attributeType) => (
                            <MenuItem key={attributeType.value} value={attributeType.value}>
                                {attributeType.label}
                            </MenuItem>
                        ))}
                        </Select>
                      </FormControl>
                    </TableCell>
                    <TableCell sx={{verticalAlign: 'top'}}>
                      <TextField
                        fullWidth
                        size="small"
                        value={item.caption}
                        onChange={handleItemTextChange(index, "caption")}
                        error={!!errors.items?.[index]?.caption}
                        helperText={errors.items?.[index]?.caption}
                      />
                    </TableCell>
                    <TableCell>
                      <IconButton onClick={() => handleRemoveItem(index)}>
                        <DeleteIcon sx={{ color: "#FF8400" }} />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mt: 4 }}>
            <Button variant="contained" color="primary" onClick={handleSubmit}>Register</Button>
            <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
            <Button variant="outlined" onClick={() => navigate(-1)}>Cancel</Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );
};

export default ZkpNamespaceRegistrationPage;
