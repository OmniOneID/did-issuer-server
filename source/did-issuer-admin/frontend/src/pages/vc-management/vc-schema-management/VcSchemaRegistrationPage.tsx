import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import DeleteIcon from "@mui/icons-material/Delete";
import { Box, Button, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme, Dialog, DialogTitle, DialogContent, DialogActions, Checkbox, styled } from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { postVcSchema } from "../../../apis/vc-management-api";
import { useDialogs } from "@toolpad/core";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import CustomDialog from "../../../components/dialog/CustomDialog";
import { fetchNamespaces } from "../../../apis/vc-management-api"; // API 변경됨
import CustomItemSelectionDialog from "./SchemaItemSelectDialog";

type Props = {}

interface VcSchemaFormData {
  vcSchemaId: string;
  title: string;
  description: string;
  items: ItemFormData[];
  version: string;
  language: string;
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
  language?: string;
  version?: string;
}

const VcSchemaRegistrationPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const [formData, setFormData] = useState<VcSchemaFormData>({
    vcSchemaId: '',
    title: '',
    description: '',
    items: [],
    version: '',
    language: ''
  });

  const [isLoading, setIsLoading] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [availableItems, setAvailableItems] = useState<ItemFormData[]>([]);
  const [selectedItems, setSelectedItems] = useState<string[]>([]);
  const [errors, setErrors] = useState<ErrorState>({});

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
    fetchItems(); // 초기 데이터 조회
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

    // 기존 선택된 아이템 중, 추가되지 않은 아이템만 유지
    setSelectedItems((prev) =>
      prev.filter((id) => availableItems.some((item) => item.id === id))
    );

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

  const handleChange = (field: keyof VcSchemaFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value;
    setFormData((prev) => ({ ...prev, [field]: newValue }));
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
    if (!vcSchemaId) return 'Please enter a VC Sche a ID.';
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
      vcSchemaId: formData.vcSchemaId,
      title: formData.title,
      description: formData.description,
      namespaces: formData.items.map(item => (item.id)),
      language: formData.language,
      version: formData.version,
    };

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register VC Schema?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      try {
        await postVcSchema(requestBody);
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Completed register VC Schema.',
          isModal: true,
        }, {
          onClose: async (result) => navigate('/vc-management/vc-schema-management'),
        });
      } catch (error) {
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: `Failed to register VC Schema: ${error}`,
          isModal: true,
        });
      }
    };
  }

  const handleReset = () => {
    setErrors({});
    setIsButtonDisabled(true);
    setFormData({ vcSchemaId: '', title: '', description: '', items: [], language: '', version: '' });
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
      <Typography variant="h4">VC Schema Management</Typography>
      <StyledContainer>
        <StyledTitle>VC Schema Registration</StyledTitle>

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
          
          <Button variant="contained" startIcon={<AddCircleOutlineIcon />} sx={{ mt: 2, mb: 2 }} onClick={handleOpenDialog}>
            Add Item
          </Button>

          <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: "auto" }}>
            <Table sx={{ tableLayout: "fixed", width: "100%" }}>
              <TableHead>
                <TableRow sx={{ backgroundColor: theme.palette.mode === "dark" ? theme.palette.background.paper : "#f5f5f5" }}>
                  <TableCell sx={{ width: 50 }}>ID</TableCell>
                  <TableCell sx={{ width: 100 }}>Namespace ID</TableCell>
                  <TableCell sx={{ width: 100 }}>Name</TableCell>
                  <TableCell sx={{ width: 50 }}>Delete</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {formData.items.map((item, index) => (
                  <TableRow key={index}>
                    <TableCell>{item.id}</TableCell>
                    <TableCell
                      sx={{ color: "blue", textDecoration: "underline", cursor: "pointer" }}
                      onClick={(e) => {
                        handleOpenNamespaceDetail(item.id);
                      }}
                    >
                      {item.namespaceId}
                    </TableCell>
                    <TableCell>{item.name}</TableCell>

                    <TableCell>
                      <IconButton onClick={() => handleRemoveItem(index)} color="error">
                        <DeleteIcon sx={{ color: '#FF8400' }}/>
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
            <Button variant="outlined" color="secondary" onClick={() => navigate('/vc-management/vc-schema-management')}>Cancel</Button>
          </Box>
        </StyledInputArea>
      </StyledContainer>

      <CustomItemSelectionDialog
        open={openDialog}
        onClose={handleCloseDialog}
        availableItems={availableItems}
        selectedItems={selectedItems}
        onSelectItem={handleSelectItem}
        onConfirmSelection={handleAddSelectedItems}
      />
    </>
  );
}

export default VcSchemaRegistrationPage;
