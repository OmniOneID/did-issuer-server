import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import DeleteIcon from "@mui/icons-material/Delete";
import { Box, Button, IconButton, MenuItem, Paper, Select, SelectChangeEvent, styled, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { postNamespace } from "../../../apis/vc-management-api";
import { useDialogs } from "@toolpad/core";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import CustomDialog from "../../../components/dialog/CustomDialog";

type Props = {}

interface NamespaceFormData {
  namespaceId: string;
  name: string;
  ref: string;
  items: ItemFormData[];
}

interface ItemFormData {
  id: string;
  type: "text" | "image" | "document";
  format: "plain" | "html" | "xml" | "csv" | "png" | "jpg" | "gif" | "txt" | "pdf" | "word";
  caption: string;
}

interface ErrorState {
  namespaceId?: string;
  name?: string;
  ref?: string;
  items?: { id?: string; type?: string; format?: string; caption?: string }[];
  errorItemsMessage?: string;
}


const NamespaceRegistrationPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const [formData, setFormData] = useState<NamespaceFormData>({
    namespaceId: '',
    name: '',
    ref: '',
    items: [],
  });

  const [errors, setErrors] = useState<ErrorState>({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (field: keyof NamespaceFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value;
    setFormData((prev) => ({ ...prev, [field]: newValue }));
  };

  const handleSelectChange = (index: number, field: keyof ItemFormData) => (event: SelectChangeEvent<string>) => {
    const newItems = [...formData.items];
    newItems[index] = { ...newItems[index], [field]: event.target.value as string };
    setFormData((prev) => ({ ...prev, items: newItems }));
  };

  const handleTextChange = (index: number, field: keyof ItemFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const newItems = [...formData.items];
    newItems[index] = { ...newItems[index], [field]: event.target.value };
    setFormData((prev) => ({ ...prev, items: newItems }));
  };

  const validate = () => {
    let tempErrors: ErrorState = {};

    tempErrors.namespaceId = validateNamespaceId(formData.namespaceId);
    tempErrors.name = validateName(formData.name);
    tempErrors.ref = validateRef(formData.ref);

    if (formData.items.length === 0) {
      tempErrors.errorItemsMessage = "At least one item is required.";
    } else {
      tempErrors.errorItemsMessage = undefined;
      tempErrors.items = formData.items.map(validateItem);
    }

    setErrors(tempErrors);

    return (
      formData.items.length > 0 &&
      Object.entries(tempErrors)
        .filter(([key]) => key !== "items")
        .every(([, error]) => !error) &&
      (tempErrors.items ?? []).every((itemErrors) =>
        Object.values(itemErrors).every((e) => !e)
      )
    );
  };

  const validateNamespaceId = (namespaceId?: string): string | undefined => {
    if (!namespaceId) return 'Please enter a Namespace ID.';
    if (namespaceId.length < 8 || namespaceId.length > 64) return 'Namespace ID must be between 8 and 64 characters.';
    return undefined;
  };

  const validateName = (name?: string): string | undefined => {
    if (!name) return 'Please enter a Name.';
    if (name.length < 2 || name.length > 64) return 'Name must be between 2 and 64 characters.';
    return undefined;
  };

  const validateRef = (ref?: string): string | undefined => {
    if (!ref) return 'Please enter a Ref.';
    if (ref.length < 4 || ref.length > 64) return 'Ref must be between 4 and 64 characters.';
    return undefined;
  };

  const validateItem = (item: ItemFormData): { id?: string; type?: string; format?: string; caption?: string } => {
    let itemErrors: { id?: string; type?: string; format?: string; caption?: string } = {};

    if (!item.id.trim()) itemErrors.id = "ID is required.";
    if (!item.type) itemErrors.type = "Type is required.";
    if (!item.format) itemErrors.format = "Format is required.";
    if (!item.caption.trim()) itemErrors.caption = "Caption is required.";

    return itemErrors;
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    const requestBody = {
      namespace: {
        id: formData.namespaceId,
        name: formData.name,
        ref: formData.ref,
      },
      items: formData.items.map(item => ({
        id: item.id,
        caption: item.caption,
        type: item.type,
        format: item.format
      }))
    };

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register Namespace?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      try {
        await postNamespace(requestBody);
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Completed register namespace.',
          isModal: true,
        }, {
          onClose: async (result) => navigate('/vc-management/namespace-management'),
        });
      } catch (error) {
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: `Failed to register namespace: ${error}`,
          isModal: true,
        });
      }
    };
  }

  const handleReset = () => {
    setErrors({});
    setIsButtonDisabled(true);
    setFormData({ namespaceId: '', name: '', ref: '', items: [] });
  };

  const handleAddItem = () => {
    setFormData((prev) => ({
      ...prev,
      items: [...prev.items, { id: "", type: "text", format: "plain", caption: "" }],
    }));
  };

  const handleRemoveItem = (index: number) => {
    const newItems = [...formData.items];
    newItems.splice(index, 1);
    setFormData((prev) => ({ ...prev, items: newItems }));
  };

  useEffect(() => {
    const isModified = Object.values(formData).some((value) => value !== '');
    setIsButtonDisabled(!isModified);
  }, [formData]);

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
        <StyledTitle>Namespace Registration</StyledTitle>
        <StyledInputArea>
          <TextField
            label="Namespace ID *"
            variant="outlined"
            margin="normal"
            size="small"
            sx={{ width: '60%' }}
            value={formData.namespaceId}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
              const value = e.target.value;
              if (!/\s/.test(value)) {
                  handleChange('namespaceId')(e);
              }
            }}
            error={!!errors.namespaceId}
            helperText={errors.namespaceId}
          />

          <TextField
            label="Name *"
            variant="outlined"
            margin="normal"
            size="small"
            sx={{ width: '60%' }}
            value={formData.name}
            onChange={handleChange('name')}
            error={!!errors.name}
            helperText={errors.name}
          />

          <TextField
            label="Ref *"
            variant="outlined"
            margin="normal"
            size="small"
            sx={{ width: '60%' }}
            value={formData.ref}
            onChange={handleChange('ref')}
            error={!!errors.ref}
            helperText={errors.ref}
          />

          <Typography variant="h6" sx={{ mt: 3 }}>Items</Typography>

          {errors.errorItemsMessage && (
            <Typography color="error" variant="caption" sx={{ mt: 1, display: "block" }}>{errors.errorItemsMessage}</Typography>
          )}

          <Button variant="contained" startIcon={<AddCircleOutlineIcon />} sx={{ mt: 2, mb: 2 }} onClick={handleAddItem}>
            Add Item
          </Button>

          <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: "auto" }}>
            <Table sx={{ tableLayout: "fixed", width: "100%" }}>
              <TableHead>
                <TableRow sx={{ backgroundColor: theme.palette.mode === "dark" ? theme.palette.background.paper : "#f5f5f5" }}>
                  <TableCell sx={{ width: 150 }}>ID *</TableCell>
                  <TableCell sx={{ width: 100 }}>Type *</TableCell>
                  <TableCell sx={{ width: 150 }}>Format *</TableCell>
                  <TableCell sx={{ width: 200 }}>Caption *</TableCell>
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
                        value={item.id}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                          const value = e.target.value;
                          if (!/\s/.test(value)) {
                              handleTextChange(index, "id")(e);
                          }
                        }}
                        error={!!errors.items?.[index]?.id}
                        helperText={errors.items?.[index]?.id}
                        sx={{ width: 150 }}
                      />
                    </TableCell>
                    <TableCell sx={{verticalAlign: 'top'}}>
                      <Select fullWidth size="small" value={item.type} onChange={handleSelectChange(index, "type")}
                        error={!!errors.items?.[index]?.type} sx={{ width: 100 }}>
                        <MenuItem value="text">Text</MenuItem>
                        <MenuItem value="image">Image</MenuItem>
                        <MenuItem value="document">Document</MenuItem>
                      </Select>
                    </TableCell>
                    <TableCell sx={{verticalAlign: 'top'}}>
                      <Select fullWidth size="small" value={item.format} onChange={handleSelectChange(index, "format")}
                        error={!!errors.items?.[index]?.format} sx={{ width: 150 }}>
                        {["plain", "html", "xml", "csv", "png", "jpg", "gif", "txt", "pdf", "word"].map((format) => (
                          <MenuItem key={format} value={format}>{format.toUpperCase()}</MenuItem>
                        ))}
                      </Select>
                    </TableCell>
                    <TableCell sx={{verticalAlign: 'top'}}>
                      <TextField
                        fullWidth
                        size="small"
                        value={item.caption}
                        onChange={handleTextChange(index, "caption")}
                        error={!!errors.items?.[index]?.caption}
                        helperText={errors.items?.[index]?.caption}
                        sx={{ width: 200 }}
                      />
                    </TableCell>
                    <TableCell sx={{ width: 50, verticalAlign: 'top' }}>
                      <IconButton onClick={() => handleRemoveItem(index)} color="error">
                        <DeleteIcon sx={{ color: '#FF8400' }} />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            <Button variant="contained" color="primary" onClick={handleSubmit} disabled={isButtonDisabled}>Register</Button>
            <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
            <Button variant="outlined" color="secondary" onClick={() => navigate('/vc-management/namespace-management')}>Cancel</Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );

}

export default NamespaceRegistrationPage;
