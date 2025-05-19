import {
  Box, Button, FormControl, IconButton, MenuItem, Paper, Select,
  SelectChangeEvent, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, TextField, Typography, styled, useTheme
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { useDialogs } from "@toolpad/core";
import CustomDialog from "../../../components/dialog/CustomDialog";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import { attributeTypes } from "../../../constants/attribute-types";
import { getZkpNamespace, patchZkpNamespace } from "../../../apis/zkp_management-api";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import DeleteIcon from "@mui/icons-material/Delete";
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
  name?: string;
  ref?: string;
  items?: ItemError[];
  errorItemsMessage?: string;
}

const ZkpNamespaceEditPage = () => {
  const { id } = useParams();
  const theme = useTheme();
  const dialogs = useDialogs();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<FormData>({
    namespaceId: "",
    name: "",
    ref: "",
    items: [],
  });
  const [originalData, setOriginalData] = useState<FormData | null>(null);
  const [errors, setErrors] = useState<ErrorState>({});
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isButtonDisabled, setIsButtonDisabled] = useState<boolean>(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await getZkpNamespace(parseInt(id as string));
        const loadedData: FormData = {
          namespaceId: data.namespace.namespaceId,
          name: data.namespace.name,
          ref: data.namespace.ref || "",
          items: data.attributes || [],
        };
        setFormData(loadedData);
        setOriginalData(loadedData);
        setIsLoading(false);
      } catch (err) {
        console.error(err);
        await dialogs.open(CustomDialog, {
          title: "Error",
          message: "Failed to load namespace data.",
          isModal: true,
        });
        navigate(-1);
      }
    };

    fetchData();
  }, [id]);

  useEffect(() => {
    if (!originalData) return;
    const hasChanged = JSON.stringify(formData) !== JSON.stringify(originalData);
    setIsButtonDisabled(!hasChanged);
  }, [formData, originalData]);

  const handleChange = (field: keyof FormData) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [field]: e.target.value }));
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

  const handleItemSelectChange = (index: number) => (e: SelectChangeEvent<string>) => {
    const newItems = [...formData.items];
    newItems[index].type = e.target.value as ItemType;
    setFormData(prev => ({ ...prev, items: newItems }));
  };

  const handleAddItem = () => {
    setFormData(prev => ({
      ...prev,
      items: [...prev.items, {
        label: "",
        type: attributeTypes[0].value as ItemType,
        caption: ""
      }]
    }));
  };

  const validate = () => {
    const tempErrors: ErrorState = {};

    if (!formData.name.trim()) {
      tempErrors.name = "Please enter a Name.";
    } else if (formData.name.length < 2 || formData.name.length > 64) {
      tempErrors.name = "Name must be 2–64 characters.";
    }

    if (formData.ref.trim()) {
      if (formData.ref.length < 4 || formData.ref.length > 64) {
        tempErrors.ref = "Ref must be 4–64 characters.";
      }
       if (!urlRegex.test(formData.ref) && !ipRegex.test(formData.ref)) {
        tempErrors.ref = "Please enter a valid URL.";
      }
    }

    if (formData.items.length === 0) {
      tempErrors.errorItemsMessage = "At least one item is required.";
    } else {
      const labelCounts = formData.items.reduce((acc, item) => {
        const key = item.label.trim();
        if (key) acc[key] = (acc[key] || 0) + 1;
        return acc;
      }, {} as Record<string, number>);

      tempErrors.items = formData.items.map(item => {
        const itemErrors: ItemError = {};
        if (!item.label.trim()) itemErrors.label = "Label is required.";
        else if (labelCounts[item.label.trim()] > 1) itemErrors.label = "Label must be unique.";

        if (!item.type) itemErrors.type = "Type is required.";

        if (!item.caption.trim()) itemErrors.caption = "Caption is required.";

        return itemErrors;
      });
    }

    setErrors(tempErrors);

    return (
      !tempErrors.name &&
      !tempErrors.ref &&
      !tempErrors.errorItemsMessage &&
      (tempErrors.items ?? []).every(err => Object.values(err).every(v => !v))
    );
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    const requestBody = {
      id: parseInt(id as string),
      namespace: {
        namespaceId: formData.namespaceId,
        name: formData.name,
        ref: formData.ref,
      },
      attributes: formData.items.map(item => ({
        label: item.label,
        type: item.type,
        caption: item.caption
      })),
    };

    console.log("Update Payload:", requestBody);

    const result = await dialogs.open(CustomConfirmDialog, {
      title: "Confirmation",
      message: "Do you want to update this namespace?",
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      try {
        await patchZkpNamespace(requestBody);
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
            title: 'Notification',
            message: 'Completed updateZKPnamespace.',
            isModal: true,
        }, {
            onClose: async (result) => navigate('/zkp-management/zkp-namespace-management'),
        });
      } catch (error) {
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
            title: 'Notification',
            message: `Failed to update ZKP namespace: ${error}`,
            isModal: true,
        });
      }
    }
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

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Typography variant="h4">ZKP Namespace Management</Typography>
      <StyledContainer>
        <StyledTitle>ZKP Namespace Update</StyledTitle>

        <TextField
          label="Namespace ID"
          fullWidth
          size="small"
          margin="normal"
          sx={{ width: '60%' }}
          value={formData.namespaceId}
          disabled
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
          <Typography color="error" variant="caption" sx={{ mt: 1 }}>
            {errors.errorItemsMessage}
          </Typography>
        )}

        <Button
          variant="contained"
          startIcon={<AddCircleOutlineIcon />}
          sx={{ my: 2 }}
          onClick={handleAddItem}
        >
          Add Item
        </Button>

        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                <TableCell sx={{ width: 200 }}>Label *</TableCell>
                <TableCell sx={{ width: 150 }}>Type *</TableCell>
                <TableCell>Caption *</TableCell>
                <TableCell sx={{ width: 80 }}>Delete</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {formData.items.map((item, index) => {
                const isInitial = Boolean(originalData && index < originalData.items.length);

                return (
                  <TableRow key={index}>
                    <TableCell sx={{verticalAlign: 'top'}}>
                      <TextField
                        fullWidth
                        size="small"
                        value={item.label}
                        onChange={handleItemTextChange(index, "label")}
                        error={!!errors.items?.[index]?.label}
                        helperText={errors.items?.[index]?.label}
                        disabled={isInitial} // 기존 row 비활성화
                      />
                    </TableCell>
                    <TableCell sx={{verticalAlign: 'top'}}>
                      <FormControl
                        fullWidth
                        size="small"
                        error={!!errors.items?.[index]?.type}
                        disabled={isInitial} // 기존 row 비활성화
                      >
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
                        disabled={isInitial} // 기존 row 비활성화
                      />
                    </TableCell>
                    <TableCell sx={{verticalAlign: 'top'}}>
                      {!isInitial && (
                        <IconButton onClick={() => {
                          const newItems = [...formData.items];
                          newItems.splice(index, 1);
                          setFormData(prev => ({ ...prev, items: newItems }));
                        }}>
                          <DeleteIcon sx={{ color: "#FF8400" }} />
                        </IconButton>
                      )}
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>

        <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mt: 4 }}>
          <Button
            variant="contained"
            color="primary"
            onClick={handleSubmit}
            disabled={isButtonDisabled}
          >
            Update
          </Button>
          <Button variant="outlined" onClick={() => navigate(-1)}>Cancel</Button>
        </Box>
      </StyledContainer>
    </>
  );
};

export default ZkpNamespaceEditPage;
