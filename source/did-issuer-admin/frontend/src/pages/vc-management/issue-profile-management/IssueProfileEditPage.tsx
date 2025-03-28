import SearchIcon from "@mui/icons-material/Search";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import { Box, Button, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme, Dialog, DialogTitle, DialogContent, DialogActions, Radio, Select, MenuItem, FormControl, InputLabel, styled, FormHelperText } from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { fetchNamespaces, fetchVcSchema, getIssueProfile, patchIssueProfile, postIssueProfile } from "../../../apis/vc-management-api";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import { useDialogs } from "@toolpad/core/useDialogs";
import CustomDialog from "../../../components/dialog/CustomDialog";
import { Language } from "@mui/icons-material";
import VcSchemaSelectionDialog from "./VcSchemaSelectionDialog";

type Props = {}

interface IssueProfileFormData {
    vcPlanId: string;
    title: string;
    description: string;
    vcSchemaId: string;
    endpoints: string[];
    cipher: string;
    curve: string;
    padding: string;
    initiateType: string;
    language: string;
    tags: string[];
}

interface ErrorState {
    vcPlanId?: string;
    title?: string;
    description?: string;
    vcSchemaId?: string;
    endpoints?: string[] | undefined;
    endpointsErrorMessage?: string;
    cipher?: string;
    curve?: string;
    padding?: string;
    initiateType?: string;
    language?: string;
    tagsErrorMessage?: string;
    tags?: string[] | undefined;
}

interface ItemFormData {
    id: string;
    vcSchemaId: string;
    title: string;
}

const cipherOptions = ["AES-128-CBC", "AES-128-ECB", "AES-256-CBC", "AES-256-ECB"];
const curveOptions = ["secp256r1"];
const paddingOptions = ["PKCS5", "OAEP"];
const initiateTypeOptions = [{ key: "User Initiate", value: "user_init" },
{ key: "Issuer Initiate", "value": "issuer_init" }
]
    ;

const IssueProfileRegistrationPage = (props: Props) => {
    const navigate = useNavigate();
    const theme = useTheme();
    const dialogs = useDialogs();

    const [formData, setFormData] = useState<IssueProfileFormData>({
        vcPlanId: '',
        title: '',
        description: '',
        vcSchemaId: '',
        endpoints: [''], // 기본적으로 1개 입력 필드 제공
        cipher: '',
        curve: '',
        padding: '',
        initiateType: '',
        language: '',
        tags: [''],
    });

    const [isLoading, setIsLoading] = useState(false);
    const [openDialog, setOpenDialog] = useState(false);
    const [availableItems, setAvailableItems] = useState<ItemFormData[]>([]);
    const [selectedItemId, setSelectedItemId] = useState<string | null>(null);

    const { id } = useParams();
    const numericIssueProfileId = id ? parseInt(id, 10) : null;

    const [errors, setErrors] = useState<ErrorState>({});
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);
    const [initialData, setInitialData] = useState<IssueProfileFormData | null>(null);

    // 서버로 데이터 전송
    const handleSubmit = async () => {
        if (!validate()) return;

        const requestBody = {
            id: numericIssueProfileId,
            vcPlanId: formData.vcPlanId,
            title: formData.title,
            description: formData.description,
            vcSchemaId: selectedItemId,
            endpoints: formData.endpoints, // 리스트 형태로 전송
            cipher: formData.cipher,
            curve: formData.curve,
            padding: formData.padding,
            initiateType: formData.initiateType,
            language: formData.language,
            tags: formData.tags,
        };

        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to register Issue Profile?',
            isModal: true,
        });
        if (result) {
            setIsLoading(true);
            try {
                await patchIssueProfile(requestBody);
                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Completed register Issue Profile.',
                    isModal: true,
                }, {
                    onClose: async (result) => navigate('/vc-management/issue-profile-management'),
                });
            } catch (error) {
                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: `Failed to register Issue Profile: ${error}`,
                    isModal: true,
                });
            }
        };
        console.log("Submitting Data:", requestBody);
        // API 호출 로직 추가 가능 (예: await postIssueProfile(requestBody))
    };


    const validate = () => {
        let tempErrors: ErrorState = {};

        tempErrors.vcPlanId = validateVcPlanId(formData.vcPlanId);
        tempErrors.title = validateTitle(formData.title);
        tempErrors.description = validateDescription(formData.description);
        tempErrors.vcSchemaId = validateVcSchemaId(formData.vcSchemaId);
        tempErrors.cipher = validateCipher(formData.cipher);
        tempErrors.curve = validateCurve(formData.curve);
        tempErrors.padding = validatePadding(formData.padding);
        tempErrors.initiateType = validateInitiateType(formData.initiateType);
        tempErrors.language = validateLanguage(formData.language)

        if (formData.endpoints.length === 0) {
            tempErrors.endpointsErrorMessage = "At least one Endpoint is required.";
        } else {
            tempErrors.endpointsErrorMessage = undefined;
            tempErrors.endpoints = formData.endpoints.map(validateEndpoint).filter(endpoint => endpoint?.length !== 0);
        }

        if (formData.tags.length === 0) {
            tempErrors.tagsErrorMessage = "At least one Tag is required.";
        } else {
            tempErrors.tagsErrorMessage = undefined;
            tempErrors.tags = formData.tags.map(validateTag).filter(tag => tag?.length !== 0);
        }
        
        setErrors(tempErrors);

        return (
            Object.entries(tempErrors)
                .filter(([key]) => key !== "endpoints" && key !== "tags")
                .every(([, error]) => !error) &&
            (tempErrors.endpoints ?? []).every((endpoint) =>
                Object.values(endpoint).every((e) => !e)) &&
            (tempErrors.tags ?? []).every((tag) =>
                Object.values(tag).every((e) => !e))
        );
    };

    const validateVcPlanId = (vcPlanId?: string): string | undefined => {
        if (!vcPlanId) return 'Please enter a VC Plan ID.';
        if (vcPlanId.length < 4 || vcPlanId.length > 64) return 'VC Plan ID must be between 4 and 64 characters.';
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

    const validateVcSchemaId = (vcSchemaId?: string): string | undefined => {
        if (!vcSchemaId) return 'Please choose a VC Schema.';
        return undefined;
    };

    const validateCipher = (cipher?: string): string | undefined => {
        if (!cipher) return 'Please choose a Cipher.';
        return undefined;
    };

    const validateCurve = (curve?: string): string | undefined => {
        if (!curve) return 'Please choose a Curve.';
        return undefined;
    };

    const validatePadding = (padding?: string): string | undefined => {
        if (!padding) return 'Please choose a Padding.';
        return undefined;
    };

    const validateInitiateType = (initiateType?: string): string | undefined => {
        if (!initiateType) return 'Please choose a Iinitiate Type.';
        return undefined;
    };

    const validateLanguage = (language?: string): string | undefined => {
        if (!language) return 'Please enter a Language.';
        if (language.length < 2 || language.length > 64) return 'Language must be between 2 and 64 characters.';
        return undefined;
    };

    const validateEndpoint = (endpoint?: string): string => {
        if (!endpoint) return 'Please enter a Endpoint.';
        if (endpoint.length < 2 || endpoint.length > 2000) return 'Endpoint must be between 2 and 2000 characters.';
        return '';
    };
    const validateTag = (tag?: string): string => {
        if (!tag) return 'Please enter a Tag.';
        if (tag.length < 2 || tag.length > 200) return 'Tag must be between 2 and 200 characters.';
        return '';
    };
    // `endpoints` 입력 필드 추가
    const handleAddEndpoint = () => {
        setFormData((prev) => ({
            ...prev,
            endpoints: [...prev.endpoints, ''],
        }));
    };

    // `endpoints` 입력 필드 제거
    const handleRemoveEndpoint = (index: number) => {
        setFormData((prev) => ({
            ...prev,
            endpoints: prev.endpoints.filter((_, i) => i !== index),
        }));
    };

    // `endpoints` 입력 값 변경
    const handleChangeEndpoint = (index: number, value: string) => {
        setFormData((prev) => {
            const newEndpoints = [...prev.endpoints];
            newEndpoints[index] = value;
            return { ...prev, endpoints: newEndpoints };
        });
    };

    // `tags` 입력 필드 추가
    const handleAddTag = () => {
        setFormData((prev) => ({
            ...prev,
            tags: [...prev.tags, ''],
        }));
    };

    // `tags` 입력 필드 제거
    const handleRemoveTag = (index: number) => {
        setFormData((prev) => ({
            ...prev,
            tags: prev.tags.filter((_, i) => i !== index),
        }));
    };

    // `tags` 입력 값 변경
    const handleChangeTag = (index: number, value: string) => {
        setFormData((prev) => {
            const newTags = [...prev.endpoints];
            newTags[index] = value;
            return { ...prev, tags: newTags };
        });
    };

    // 서버에서 데이터 가져오기
    const fetchItems = async () => {
        try {
            fetchVcSchema(0, 10, null, null)
                .then((response) => {
                    setAvailableItems(response.data.content || []);
                })
                .catch((error) => {
                    console.error("Failed to retrieve VC Schemas. ", error);
                    navigate('/error', { state: { message: `Failed to retrieve VC Schemas: ${error}` } });
                });
        } catch (error) {
            console.error("Failed to fetch VC Schemas", error);
        }
    };

    // 다이얼로그 열기
    const handleOpenDialog = () => {
        fetchItems(); // 데이터 조회

        // 기존 `vcSchemaId` 값을 유지하여 선택된 상태로 유지
        setSelectedItemId(formData.vcSchemaId || null);

        setOpenDialog(true);
    };

    // 다이얼로그 닫기
    const handleCloseDialog = () => {
        setOpenDialog(false);
    };

    // 단일 선택 (Radio 버튼)
    const handleSelectItem = (formData: ItemFormData) => {
        setSelectedItemId(formData.id);
    };

    // 선택한 항목을 VC Schema ID에 설정
    const handleAddSelectedItem = () => {
        if (!selectedItemId) return;

        const selectedItem = availableItems.find((item) => item.id === selectedItemId);
        if (!selectedItem) return;

        setFormData((prev) => ({
            ...prev,
            vcSchemaId: selectedItem.vcSchemaId,
        }));

        handleCloseDialog();
    };

    // Select Box 값 변경 핸들러
    const handleSelectChange = (field: keyof IssueProfileFormData) => (event: any) => {
        setFormData((prev) => ({
            ...prev,
            [field]: event.target.value,
        }));
    };

    const handleOpenVcSchemaDetail = (vcSchemaId: string) => {
        window.open(`/vc-management/vc-schema-management-popup/${vcSchemaId}?isPopup=true`, "vc schema detail", "popup=yes, width=850, height=800");
    };

    const handleReset = () => {
        if (initialData) {
            setFormData(initialData);
            setIsButtonDisabled(true);
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            if (numericIssueProfileId === null || isNaN(numericIssueProfileId)) {
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
                const { data } = await getIssueProfile(numericIssueProfileId);
                console.log(data)
                const issueProfileData = ({
                    vcPlanId: data.issueProfile.vcPlanId,
                    title: data.issueProfile.title,
                    description: data.issueProfile.description,
                    vcSchemaId: data.vcSchemaName,
                    endpoints: data.issueProfile.endpoints, // 리스트 형태로 전송
                    cipher: data.issueProfile.cipher,
                    curve: data.issueProfile.curve,
                    padding: data.issueProfile.padding,
                    initiateType: data.issueProfile.initiateType,
                    language: data.issueProfile.language,
                    tags: data.issueProfile.tags,
                });

                setSelectedItemId(data.issueProfile.vcSchemaId);
                setFormData(issueProfileData);
                setInitialData(issueProfileData);
                setIsButtonDisabled(true);
                setIsLoading(false);
            } catch (err) {
                console.error("Failed to fetch Namespace information:", err);
                setIsLoading(false);
                navigate("/error", { state: { message: `Failed to fetch namespace information: ${err}` } });
            }
        };

        fetchData();
    }, [numericIssueProfileId]);

    useEffect(() => {
        if (!initialData) return;
        const isModified = JSON.stringify(formData) !== JSON.stringify(initialData);
        setIsButtonDisabled(!isModified);
    }, [formData, initialData]);

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
            <Typography variant="h4">Issue Profile Management</Typography>
            <StyledContainer>
                <StyledTitle>Issue Profile Update</StyledTitle>

                <StyledInputArea>
                    <TextField label="VC Plan ID *" fullWidth margin="normal" size="small" value={formData.vcPlanId} error={!!errors.vcPlanId} helperText={errors.vcPlanId} onChange={(e) => setFormData({ ...formData, vcPlanId: e.target.value })} />
                    <TextField label="Title *" fullWidth margin="normal" size="small" value={formData.title} error={!!errors.title} helperText={errors.title} onChange={(e) => setFormData({ ...formData, title: e.target.value })} />
                    <TextField label="Description" fullWidth margin="normal" size="small" value={formData.description} error={!!errors.description} helperText={errors.description} onChange={(e) => setFormData({ ...formData, description: e.target.value })} />

                    {/* VC Schema ID 입력 필드 + 찾기 버튼 */}
                    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                        <TextField label="VC Schema ID *" fullWidth margin="normal" size="small" value={formData.vcSchemaId} error={!!errors.vcSchemaId} helperText={errors.vcSchemaId} disabled />
                        <IconButton color="primary" onClick={handleOpenDialog}><SearchIcon /></IconButton>
                    </Box>

                    <FormControl fullWidth size="small" sx={{ maxWidth: 800, margin: 'auto', mt: 2, }}>
                        <InputLabel>Initiate Type *</InputLabel>
                        <Select label="Initiate Type *" value={formData.initiateType} error={!!errors.initiateType} onChange={handleSelectChange("initiateType")}>
                            {initiateTypeOptions.map((option) => <MenuItem key={option.key} value={option.value}> {option.key}</MenuItem>)}
                        </Select>
                        <FormHelperText error>{errors.initiateType}</FormHelperText>
                    </FormControl>
                    <TextField label="Language *" fullWidth margin="normal" size="small" error={!!errors.language} helperText={errors.language} value={formData.language} onChange={(e) => setFormData({ ...formData, language: e.target.value })} />
                    {/* Endpoints 입력 필드 */}
                    <Typography variant="h6" sx={{ mt: 3 }}>Endpoints *</Typography>
                    {errors.endpointsErrorMessage && (
                        <Typography color="error" variant="caption" sx={{ mt: 1, display: "block" }}>{errors.endpointsErrorMessage}</Typography>
                    )}

                    {formData.endpoints.map((endpoint, index) => (
                        <Box key={index} sx={{ display: "flex", alignItems: "center", gap: 1, mt: 1 }}>
                            <TextField fullWidth
                                size="small"
                                value={endpoint}
                                onChange={(e) => handleChangeEndpoint(index, e.target.value)}
                                error={!!errors.endpoints?.[index]}
                                helperText={errors.endpoints?.[index]}
                            />
                            <IconButton color="error" onClick={() => handleRemoveEndpoint(index)}><RemoveCircleOutlineIcon sx={{ color: '#FF8400' }} /></IconButton>
                        </Box>
                    ))}
                    <Button startIcon={<AddCircleOutlineIcon />} sx={{ mt: 1 }} onClick={handleAddEndpoint}>Add Endpoint</Button>

                    <Typography variant="h6" sx={{ mt: 3 }}>E2E</Typography>
                    <FormControl fullWidth size="small" sx={{ margin: 'auto', mt: 2, }}>
                        <InputLabel>Cipher *</InputLabel>
                        <Select label="Cipher *" value={formData.cipher} error={!!errors.cipher} onChange={handleSelectChange("cipher")}>
                            {cipherOptions.map((option) => <MenuItem key={option} value={option}>{option}</MenuItem>)}
                        </Select>
                        <FormHelperText error>{errors.cipher}</FormHelperText>
                    </FormControl>

                    <FormControl fullWidth size="small" sx={{ margin: 'auto', mt: 2, }}>
                        <InputLabel>Curve *</InputLabel>
                        <Select label="Curv *" value={formData.curve} error={!!errors.curve} onChange={handleSelectChange("curve")}>
                            {curveOptions.map((option) => <MenuItem key={option} value={option}>{option}</MenuItem>)}
                        </Select>
                        <FormHelperText error>{errors.curve}</FormHelperText>
                    </FormControl>

                    <FormControl fullWidth size="small" sx={{ margin: 'auto', mt: 2, }}>
                        <InputLabel>Padding *</InputLabel>
                        <Select label="Padding *" value={formData.padding} error={!!errors.padding} onChange={handleSelectChange("padding")}>
                            {paddingOptions.map((option) => <MenuItem key={option} value={option}>{option}</MenuItem>)}
                        </Select>
                        <FormHelperText error>{errors.padding}</FormHelperText>
                    </FormControl>

                    <Typography variant="h6" sx={{ mt: 3 }}>Tags *</Typography>
                    {errors.tagsErrorMessage && (
                        <Typography color="error" variant="caption" sx={{ mt: 1, display: "block" }}>{errors.tagsErrorMessage}</Typography>
                    )}

                    {formData.tags.map((tag, index) => (
                        <Box key={index} sx={{ display: "flex", alignItems: "center", gap: 1, mt: 1 }}>
                            <TextField fullWidth
                                size="small"
                                value={tag}
                                onChange={(e) => handleChangeTag(index, e.target.value)}
                                error={!!errors.tags?.[index]}
                                helperText={errors.tags?.[index]}
                            />
                            <IconButton color="error" onClick={() => handleRemoveTag(index)}><RemoveCircleOutlineIcon sx={{ color: '#FF8400' }} /></IconButton>
                        </Box>
                    ))}
                    <Button startIcon={<AddCircleOutlineIcon />} sx={{ mt: 1 }} onClick={handleAddTag}>Add Tag</Button>

                    <VcSchemaSelectionDialog
                        open={openDialog}
                        onClose={handleCloseDialog}
                        availableItems={availableItems}
                        selectedItemId={selectedItemId}
                        onSelectItem={handleSelectItem}
                        onConfirmSelection={handleAddSelectedItem}
                    />

                    <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mt: 3 }}>
                        <Button variant="contained" color="primary" disabled={isButtonDisabled} onClick={handleSubmit}>Update</Button>
                        <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
                        <Button variant="outlined" color="primary" onClick={() => navigate(-1)}>Back</Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    );
}

export default IssueProfileRegistrationPage;