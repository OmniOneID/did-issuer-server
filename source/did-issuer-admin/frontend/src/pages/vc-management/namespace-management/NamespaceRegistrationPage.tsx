import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import DeleteIcon from "@mui/icons-material/Delete";
import { Box, Button, IconButton, MenuItem, Paper, Select, SelectChangeEvent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";

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
}


const NamespaceRegistrationPage = (props: Props) => {
    const navigate = useNavigate();
    const theme = useTheme(); // 다크 모드 지원

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
      tempErrors.items = formData.items.map(validateItem);

      setErrors(tempErrors);
      return (
        Object.values(tempErrors).every((error) => !error) &&
        tempErrors.items.every((itemErrors) => Object.values(itemErrors ?? {}).every((e) => !e))
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
      console.log("Submitted Data:", formData);
    };

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

    return (
      <>
        <FullscreenLoader open={isLoading} />
        <Box sx={{ p: 3 }}>
          <Typography variant="h4">Namespace Registration</Typography>
    
          <Box sx={{ maxWidth: 800, margin: 'auto', mt: 2, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
            {/* 테이블 상단 TextField 길이 조정 */}
            <TextField label="Namespace ID" variant="outlined" margin="normal" size="small"
              sx={{ width: '60%' }} 
              value={formData.namespaceId} onChange={handleChange('namespaceId')} 
              error={!!errors.namespaceId} helperText={errors.namespaceId} 
            />
    
            <TextField label="Name" variant="outlined" margin="normal" size="small"
              sx={{ width: '60%' }} 
              value={formData.name} onChange={handleChange('name')} 
              error={!!errors.name} helperText={errors.name} 
            />
    
            <TextField label="Ref" variant="outlined" margin="normal" size="small"
              sx={{ width: '60%' }} 
              value={formData.ref} onChange={handleChange('ref')} 
              error={!!errors.ref} helperText={errors.ref} 
            />
    
            <Typography variant="h6" sx={{ mt: 3 }}>Items</Typography>
            <Button variant="contained" startIcon={<AddCircleOutlineIcon />} sx={{ mt: 2, mb: 2 }} onClick={handleAddItem}>
              Add Item
            </Button>
    
            <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: "auto" }}>
              <Table sx={{ tableLayout: "fixed", width: "100%" }}>
                <TableHead>
                  <TableRow sx={{ backgroundColor: theme.palette.mode === "dark" ? theme.palette.background.paper : "#f5f5f5" }}>
                    <TableCell sx={{ width: 150 }}>ID</TableCell>
                    <TableCell sx={{ width: 100 }}>Type</TableCell>
                    <TableCell sx={{ width: 150 }}>Format</TableCell>
                    <TableCell sx={{ width: 200 }}>Caption</TableCell>
                    <TableCell sx={{ width: 100 }}>Delete</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {formData.items.map((item, index) => (
                    <TableRow key={index}>
                      <TableCell>
                        <TextField fullWidth size="small" value={item.id} onChange={handleTextChange(index, "id")}
                          error={!!errors.items?.[index]?.id} helperText={errors.items?.[index]?.id}
                          sx={{ width: 150 }} />
                      </TableCell>
                      <TableCell>
                        <Select fullWidth size="small" value={item.type} onChange={handleSelectChange(index, "type")}
                          error={!!errors.items?.[index]?.type} sx={{ width: 100 }}>
                          <MenuItem value="text">Text</MenuItem>
                          <MenuItem value="image">Image</MenuItem>
                          <MenuItem value="document">Document</MenuItem>
                        </Select>
                      </TableCell>
                      <TableCell>
                        <Select fullWidth size="small" value={item.format} onChange={handleSelectChange(index, "format")}
                          error={!!errors.items?.[index]?.format} sx={{ width: 150 }}>
                          {["plain", "html", "xml", "csv", "png", "jpg", "gif", "txt", "pdf", "word"].map((format) => (
                            <MenuItem key={format} value={format}>{format.toUpperCase()}</MenuItem>
                          ))}
                        </Select>
                      </TableCell>
                      <TableCell>
                        <TextField fullWidth size="small" value={item.caption} onChange={handleTextChange(index, "caption")}
                          error={!!errors.items?.[index]?.caption} helperText={errors.items?.[index]?.caption}
                          sx={{ width: 200 }} />
                      </TableCell>
                      <TableCell sx={{width: 50}}>
                        <IconButton onClick={() => handleRemoveItem(index)} color="error">
                          <DeleteIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
    
            <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
              <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
              <Button variant="contained" color="primary" onClick={handleSubmit} disabled={isButtonDisabled}>Register</Button>
            </Box>
          </Box>
        </Box>
      </>
    );
    
}

export default NamespaceRegistrationPage;
