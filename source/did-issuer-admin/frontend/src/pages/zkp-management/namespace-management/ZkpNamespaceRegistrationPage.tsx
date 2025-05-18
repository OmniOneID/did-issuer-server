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
  const navigate = useNavigate();

  const [formData, setFormData] = useState<FormData>({
    namespaceId: "",
    name: "",
    ref: "",
    items: [],
  });

  const [errors, setErrors] = useState<ErrorState>({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (field: keyof FormData) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData((prev) => ({ ...prev, [field]: e.target.value }));
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
      items: [...prev.items, { label: "", type: "String", caption: "" }],
    }));
  };

  const handleRemoveItem = (index: number) => {
    const newItems = [...formData.items];
    newItems.splice(index, 1);
    setFormData((prev) => ({ ...prev, items: newItems }));
  };

  const validateItem = (item: Item): { label?: string; type?: string; caption?: string } => {
    const itemErrors: { label?: string; type?: string; caption?: string } = {};

    if (!item.label.trim()) itemErrors.label = "Label is required.";
    if (!item.type) itemErrors.type = "Type is required.";
    if (!item.caption.trim()) itemErrors.caption = "Caption is required.";

    return itemErrors;
  };

  const validate = () => {
    const tempErrors: ErrorState = {};

    if (!formData.namespaceId.trim()) {
      tempErrors.namespaceId = "Invalid input.";
    } else if (formData.namespaceId.length < 8 || formData.namespaceId.length > 64) {
      tempErrors.namespaceId = "Namespace ID must be between 8 and 64 characters.";
    }

    if (!formData.name.trim()) {
      tempErrors.name = "Invalid input.";
    } else if (formData.name.length < 2 || formData.name.length > 64) {
      tempErrors.name = "Name must be between 2 and 64 characters.";
    }

    if (formData.ref.trim()) {
      if (formData.ref.length < 4 || formData.ref.length > 64) {
        tempErrors.ref = "Ref must be between 4 and 64 characters.";
      }
    } else {
      tempErrors.ref = undefined;
    }

    if (formData.items.length === 0) {
      tempErrors.errorItemsMessage = "At least one item is required.";
    } else {
      tempErrors.items = formData.items.map(validateItem);
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
    setIsButtonDisabled(!isModified);
  }, [formData]);

  const handleSubmit = () => {
    if (!validate()) return;
    // setIsLoading(true);

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
        <StyledTitle>Namespace 등록</StyledTitle>

        <StyledInputArea>
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
                  <TableCell sx={{ width: 100 }}>삭제</TableCell>
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
                          <MenuItem value="String">String</MenuItem>
                          <MenuItem value="Number">Number</MenuItem>
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
            <Button variant="contained" color="primary" onClick={handleSubmit} disabled={isButtonDisabled}>등록</Button>
            <Button variant="outlined" onClick={() => navigate(-1)}>취소</Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>
    </>
  );
};

export default ZkpNamespaceRegistrationPage;
