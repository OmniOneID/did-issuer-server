import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import DeleteIcon from "@mui/icons-material/Delete";
import { Box, Button, FormControl, FormHelperText, IconButton, MenuItem, Paper, Select, SelectChangeEvent, styled, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { getNamespace, patchNamespace } from "../../../apis/vc-management-api";
import { useDialogs } from "@toolpad/core";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import CustomDialog from "../../../components/dialog/CustomDialog";

type Props = {};

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

const NamespaceEditPage = (props: Props) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const theme = useTheme();

    const numericNamespaceId = id ? parseInt(id, 10) : null;
    const [formData, setFormData] = useState<NamespaceFormData>({
        namespaceId: "",
        name: "",
        ref: "",
        items: [],
    });

    const [initialData, setInitialData] = useState<NamespaceFormData | null>(null);
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [errors, setErrors] = useState<ErrorState>({});

    const handleChange = (field: keyof NamespaceFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = event.target.value;
        setFormData((prev) => ({ ...prev, [field]: newValue }));
    };

    const handleSelectChange = (index: number, field: keyof ItemFormData) => (event: SelectChangeEvent<string>) => {
        const newItems = [...formData.items];
        newItems[index] = { ...newItems[index], [field]: event.target.value };
        setFormData((prev) => ({ ...prev, items: newItems }));
    };

    const handleTextChange = (index: number, field: keyof ItemFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
        const newItems = [...formData.items];
        newItems[index] = { ...newItems[index], [field]: event.target.value };
        setFormData((prev) => ({ ...prev, items: newItems }));
    };

    const handleReset = () => {
        if (initialData) {
            setFormData(initialData);
            setIsButtonDisabled(true);
        }
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
            id: numericNamespaceId,
            schemaClaims: {
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
            }
        };

        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to update Namespace?',
            isModal: true,
        });

        if (result) {
            setIsLoading(true);
            try {
                await patchNamespace(requestBody);
                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Completed update namespace.',
                    isModal: true,
                }, {
                    onClose: async (result) => navigate('/vc-management/namespace-management'),
                });
            } catch (error) {
                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: `Failed to update namespace: ${error}`,
                    isModal: true,
                });
            }
        }
    };

    const handleRemoveItem = (index: number) => {
        const newItems = [...formData.items];
        newItems.splice(index, 1);
        setFormData((prev) => ({ ...prev, items: newItems }));
    };

    const handleAddItem = () => {
        setFormData((prev) => ({
            ...prev,
            items: [...prev.items, { id: "", type: "text", format: "plain", caption: "" }],
        }));
    };

    useEffect(() => {
        const fetchData = async () => {
            if (numericNamespaceId === null || isNaN(numericNamespaceId)) {
                await dialogs.open(CustomDialog, {
                    title: "Notification",
                    message: "Invalid Path.",
                    isModal: true,
                }, {
                    onClose: async () => navigate("/vc-management/namespace-management", { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getNamespace(numericNamespaceId);
                const namespaceData: NamespaceFormData = {
                    namespaceId: data.namespaceId,
                    name: data.name,
                    ref: data.ref,
                    items: data.schemaClaims.items,
                };

                setFormData(namespaceData);
                setInitialData(namespaceData);
                setIsButtonDisabled(true);
                setIsLoading(false);
            } catch (err) {
                console.error("Failed to fetch Namespace information:", err);
                setIsLoading(false);
                navigate("/error", { state: { message: `Failed to fetch namespace information: ${err}` } });
            }
        };

        fetchData();
    }, [numericNamespaceId]);

    useEffect(() => {
        if (!initialData) return;
        const isModified = JSON.stringify(formData) !== JSON.stringify(initialData);
        setIsButtonDisabled(!isModified);
    }, [formData, initialData]);

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
                <StyledTitle>Namespace Update</StyledTitle>
                <StyledInputArea>
                    <TextField
                        label="Namespace ID *"
                        variant="outlined"
                        margin="normal"
                        size="small"
                        disabled
                        sx={{ width: '60%' }}
                        value={formData.namespaceId}
                        onChange={handleChange('namespaceId')}
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

                    <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: "auto", mt: 2 }}>
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
                                                onChange={handleTextChange(index, "id")}
                                                error={!!errors.items?.[index]?.id}
                                                helperText={errors.items?.[index]?.id}
                                                sx={{ width: 150 }}
                                            />
                                        </TableCell>
                                        <TableCell sx={{verticalAlign: 'top'}}>
                                            <Select
                                                fullWidth
                                                size="small"
                                                value={item.type}
                                                onChange={handleSelectChange(index, "type")}
                                            >
                                                <MenuItem value="text">Text</MenuItem>
                                                <MenuItem value="image">Image</MenuItem>
                                                <MenuItem value="document">Document</MenuItem>
                                            </Select>
                                        </TableCell>
                                        <TableCell sx={{verticalAlign: 'top'}}>
                                            <Select
                                                fullWidth size="small"
                                                value={item.format}
                                                onChange={handleSelectChange(index, "format")}
                                            >
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
                                        <TableCell sx={{ verticalAlign: 'top', width: 50 }}>
                                            <IconButton onClick={() => handleRemoveItem(index)} color="error">
                                                <DeleteIcon sx={{ color: '#FF8400' }} />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>

                    <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mt: 3 }}>
                        <Button variant="contained" color="primary" disabled={isButtonDisabled} onClick={handleSubmit}>Update</Button>
                        <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
                        <Button variant="outlined" color="secondary" onClick={() => navigate('/vc-management/namespace-management')}>Cancel</Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    );
};

export default NamespaceEditPage;
