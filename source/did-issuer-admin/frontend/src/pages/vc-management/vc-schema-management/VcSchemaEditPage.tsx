import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import DeleteIcon from "@mui/icons-material/Delete";
import { Box, Button, FormControl, FormHelperText, IconButton, MenuItem, Paper, Select, SelectChangeEvent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme, Dialog, DialogTitle, DialogContent, DialogActions, Checkbox, styled } from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { fetchNamespaces, getVcSchema, patchVcSchema } from "../../../apis/vc-management-api";
import { useDialogs } from "@toolpad/core";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import CustomDialog from "../../../components/dialog/CustomDialog";
import SchemaItemSelectDialog from "./SchemaItemSelectDialog";

type Props = {};

interface VcSchemaFormData {
    vcSchemaId: string;
    title: string;
    description: string;
    items: ItemFormData[];
    language: string;
    version: string;
}

interface ItemFormData {
    id: string;
    namespaceId: string;
    name: string;
}

interface ErrorState {
    vcSchemaId?: string;
    title?: string;
    description?: string;
    items?: { id?: string; type?: string; format?: string; caption?: string }[];
    errorItemsMessage?: string;
    version?: string;
    language?: string;
}


const VcSchemaEditPage = (props: Props) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const theme = useTheme();

    const numericVcSchemaId = id ? parseInt(id, 10) : null;
    const [formData, setFormData] = useState<VcSchemaFormData>({
        vcSchemaId: '',
        title: '',
        description: '',
        items: [],
        version: '',
        language: '',
    });
    const [initialData, setInitialData] = useState<VcSchemaFormData | null>(null);
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [errors, setErrors] = useState<ErrorState>({});
    const [selectedItems, setSelectedItems] = useState<string[]>([]);
    const [availableItems, setAvailableItems] = useState<ItemFormData[]>([]);

    const [openDialog, setOpenDialog] = useState(false);


    const handleChange = (field: keyof VcSchemaFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = event.target.value;
        setFormData((prev) => ({ ...prev, [field]: newValue }));
    };

    const handleReset = () => {
        if (initialData) {
            setFormData(initialData);
            setIsButtonDisabled(true);
        }
    };

    const validate = () => {

        let tempErrors: ErrorState = {};

        tempErrors.vcSchemaId = validateVcSchemaId(formData.vcSchemaId);
        tempErrors.title = validateTitle(formData.title);
        tempErrors.description = validateDescription(formData.description);

        if (formData.items.length === 0) {
            tempErrors.errorItemsMessage = "At least one item is required.";
        } else {
            tempErrors.items = formData.items.map(validateItem);
        }

        setErrors(tempErrors);

        return (
            formData.items.length > 0 &&
            Object.entries(tempErrors)
                .filter(([key]) => key !== "items" && key !== "errorItemsMessage")
                .every(([, error]) => !error) &&
            (tempErrors.items ?? []).every((itemErrors) =>
                Object.values(itemErrors).every((e) => !e)
            )
        );
    };

    const validateVcSchemaId = (vcSchemaId?: string): string | undefined => {
        if (!vcSchemaId) return 'Please enter a VC Schema ID.';
        if (vcSchemaId.length < 2 || vcSchemaId.length > 1000) return 'VC Schema ID ID must be between 2 and 1000 characters.';
        return undefined;
    };

    const validateTitle = (title?: string): string | undefined => {
        if (!title) return 'Please enter a Title.';
        if (title.length < 4 || title.length > 64) return 'Title must be between 4 and 64 characters.';
        return undefined;
    };

    const validateDescription = (description?: string): string | undefined => {
        if (!description) return;
        if (description.length > 2000) return 'Description must be 2000 characters or less.';
        return undefined;
    };

    const validateItem = (item: ItemFormData): { id?: string; namespaceId?: string; name?: string } => {
        let itemErrors: { id?: string; namespaceId?: string; name?: string } = {};

        const idStr = typeof item.id === "number" ? String(item.id) : item.id;

        if (!idStr || typeof idStr !== "string" || idStr.trim() === "") {
            itemErrors.id = "ID is required.";
        }
        if (!item.namespaceId || typeof item.namespaceId !== "string" || item.namespaceId.trim() === "") {
            itemErrors.namespaceId = "Namespace ID is required.";
        }
        if (!item.name || typeof item.name !== "string" || item.name.trim() === "") {
            itemErrors.name = "Name is required.";
        }

        return itemErrors;
    };

    const handleSubmit = async () => {
        if (!validate()) return;

        const requestBody = {
            id: numericVcSchemaId,
            vcSchemaId: formData.vcSchemaId,
            title: formData.title,
            description: formData.description,
            namespaces: formData.items.map(item => (item.id)),
            language: formData.language,
            version: formData.version,
        };

        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to update VC Schema?',
            isModal: true,
        });

        if (result) {
            setIsLoading(true);
            try {
                await patchVcSchema(requestBody);
                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Completed update VC Schema.',
                    isModal: true,
                }, {
                    onClose: async (result) => navigate('/vc-management/vc-schema-management'),
                });
            } catch (error) {
                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: `Failed to update VC Schema: ${error}`,
                    isModal: true,
                });
            }
        }
    };

    // 서버에서 데이터 가져오기
    const fetchItems = async (search: string = "") => {
        try {
            fetchNamespaces(0, 10, null, null)
                .then((response) => {
                    setAvailableItems(response.data.content || []); // API 응답 구조에 따라 수정
                })
                .catch((error) => {
                    console.error("Failed to retrieve namespaces. ", error);
                    navigate('/error', { state: { message: `Failed to retrieve Namespaces: ${error}` } });
                })

        } catch (error) {
            console.error("Failed to fetch items", error);
        }
    };

    // 다이얼로그 열기
    const handleOpenDialog = () => {
        fetchItems(); // 데이터 조회

        // 기존 `items`의 ID를 `selectedItems`에 설정
        setSelectedItems(formData.items.map((item) => item.id));

        setOpenDialog(true);
    };

    // 다이얼로그 닫기
    const handleCloseDialog = () => {
        setOpenDialog(false);
    };

    // 체크박스로 아이템 선택
    const handleSelectItem = (id: string) => {
        setSelectedItems((prev) =>
            prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
        );
    };

    // 선택한 항목 추가
    const handleAddSelectedItems = () => {
        const newItems = availableItems.filter((item) => selectedItems.includes(item.id));

        setFormData((prev) => {
            const existingItemIds = new Set(prev.items.map(item => item.id));
            const filteredNewItems = newItems.filter(item => !existingItemIds.has(item.id));

            return {
                ...prev,
                items: [...prev.items, ...filteredNewItems],
            };
        });

        handleCloseDialog();
    };

    // 기존 아이템 제거
    const handleRemoveItem = (index: number) => {
        setFormData((prev) => {
            const removedItem = prev.items[index];

            return {
                ...prev,
                items: prev.items.filter((_, i) => i !== index),
            };
        });

        // 선택 목록에서도 제거
        setSelectedItems((prev) => prev.filter((id) => id !== formData.items[index].id));
    };

    // namespaceId 클릭 시 상세 정보 페이지 새창 열기
    const handleOpenNamespaceDetail = (namespaceId: string) => {
        window.open(`/vc-management/namespace-management-popup/${namespaceId}?isPopup=true`, "namespace detail", "popup=yes, width=850, height=800");
    };

    useEffect(() => {
        const fetchData = async () => {
            if (numericVcSchemaId === null || isNaN(numericVcSchemaId)) {
                await dialogs.open(CustomDialog, {
                    title: "Notification",
                    message: "Invalid Path.",
                    isModal: true,
                }, {
                    onClose: async () => navigate("/vc-management/vc-schema-management", { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getVcSchema(numericVcSchemaId);

                const vcSchemaData = ({
                    vcSchemaId: data.vcSchema.vcSchemaId,
                    title: data.vcSchema.title,
                    description: data.vcSchema.description,
                    items: data.items,
                    version: data.vcSchema.version,
                    language: data.vcSchema.language
                });

                setFormData(vcSchemaData);
                setInitialData(vcSchemaData);
                setIsButtonDisabled(true);
                setIsLoading(false);
            } catch (err) {
                console.error("Failed to fetch Namespace information:", err);
                setIsLoading(false);
                navigate("/error", { state: { message: `Failed to fetch namespace information: ${err}` } });
            }
        };

        fetchData();
    }, [numericVcSchemaId]);

    useEffect(() => {
        if (!initialData) return;
        console.log(formData);
        console.log(initialData);
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
            <Typography variant="h4">VC Schema Management</Typography>
            <StyledContainer>
                <StyledTitle>VC Schema Update</StyledTitle>

                <StyledInputArea>
                    <TextField
                        label="VC Schema ID *"
                        variant="outlined"
                        margin="normal"
                        size="small"
                        sx={{ width: '60%' }}
                        value={formData.vcSchemaId}
                        onChange={handleChange('vcSchemaId')}
                        error={!!errors.vcSchemaId}
                        helperText={errors.vcSchemaId}
                        disabled
                    />

                    <TextField
                        label="Title *"
                        variant="outlined"
                        margin="normal"
                        size="small"
                        sx={{ width: '60%' }}
                        value={formData.title}
                        onChange={handleChange('title')}
                        error={!!errors.title}
                        helperText={errors.title}
                    />

                    <TextField
                        label="Language *"
                        variant="outlined"
                        margin="normal"
                        size="small"
                        sx={{ width: '60%' }}
                        value={formData.language}
                        onChange={handleChange('language')}
                        error={!!errors.language}
                        helperText={errors.language}
                    />


                    <TextField
                        label="Version *"
                        variant="outlined"
                        margin="normal"
                        size="small"
                        sx={{ width: '60%' }}
                        value={formData.version}
                        onChange={handleChange('version')}
                        error={!!errors.version}
                        helperText={errors.version}
                    />

                    <TextField
                        label="Description"
                        variant="outlined"
                        margin="normal"
                        size="small"
                        multiline
                        sx={{ width: '60%' }}
                        value={formData.description}
                        onChange={handleChange('description')}
                        error={!!errors.description}
                        helperText={errors.description}
                    />

                    <Typography variant="h6" sx={{ mt: 3 }}>Items</Typography>
                    {errors.errorItemsMessage && (
                        <Typography color="error" variant="caption" sx={{ mt: 1, display: "block" }}>{errors.errorItemsMessage}</Typography>
                    )}

                    <Button variant="contained" startIcon={<AddCircleOutlineIcon />} sx={{ mt: 2, mb: 1 }} onClick={handleOpenDialog}>
                        Add Item
                    </Button>

                    <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: "auto", mt: 2 }}>
                        <Table sx={{ tableLayout: "fixed", width: "100%" }}>
                            <TableHead>
                                <TableRow sx={{ backgroundColor: theme.palette.mode === "dark" ? theme.palette.background.paper : "#f5f5f5" }}>
                                    <TableCell sx={{ width: 100 }}>ID</TableCell>
                                    <TableCell sx={{ width: 150 }}>Namespace ID</TableCell>
                                    <TableCell sx={{ width: 100 }}>Name</TableCell>
                                    <TableCell sx={{ width: 100 }}>Delete</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {formData.items.map((item, index) => (
                                    <TableRow key={index}>
                                        <TableCell>{item.id}</TableCell>
                                        <TableCell
                                            sx={{ color: "blue", textDecoration: "underline", cursor: "pointer" }}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleOpenNamespaceDetail(item.id);
                                            }}
                                        >
                                            {item.namespaceId}
                                        </TableCell>
                                        <TableCell>{item.name}</TableCell>
                                        <TableCell sx={{ width: 50 }}>
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
                        <Button variant="contained" color="secondary" onClick={() => navigate(-1)}>Back</Button>
                        <Button variant="outlined" color="secondary" onClick={handleReset}>Reset</Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>

            <SchemaItemSelectDialog
                open={openDialog}
                onClose={handleCloseDialog}
                availableItems={availableItems}
                selectedItems={selectedItems}
                onSelectItem={handleSelectItem}
                onConfirmSelection={handleAddSelectedItems}
            />
        </>
    );
};

export default VcSchemaEditPage;
