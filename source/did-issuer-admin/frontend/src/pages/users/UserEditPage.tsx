import SearchIcon from "@mui/icons-material/Search";
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Paper, Radio, styled, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router";
import FullscreenLoader from "../../components/loading/FullscreenLoader";
import { getUserInfo, patchUserInfo } from "../../apis/admin-api"; // 수정용 API 추가
import { fetchVcSchema } from "../../apis/vc-management-api";
import { useDialogs } from "@toolpad/core";
import CustomConfirmDialog from "../../components/dialog/CustomConfirmDialog";
import CustomDialog from "../../components/dialog/CustomDialog";
import VcSchemaSelectionDialog from "../vc-management/issue-profile-management/VcSchemaSelectionDialog";
import { formatErrorMessage } from "../../utils/error-handler";

interface UserFormData {
  did: string;
  vcSchemaId: string;
  userInfo: string;
  pii: string;
}

interface ItemFormData {
  id: string;
  vcSchemaId: string;
  title: string;
}

interface ErrorState {
  did?: string;
  vcSchemaId?: string;
  firstName?: string;
  lastName?: string;
  userInfo?: string;
}

const UserEditPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [errors, setErrors] = useState<ErrorState>({});
  const numerirUserId = id ? parseInt(id, 10) : null;
  const [formData, setFormData] = useState<UserFormData>({
    did: '',
    vcSchemaId: '',
    userInfo: '',
    pii: '',
  });

  const [initialData, setInitialData] = useState<UserFormData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [availableItems, setAvailableItems] = useState<ItemFormData[]>([]);
  const [selectedItemId, setSelectedItemId] = useState<string | null>(null);
  const [openDialog, setOpenDialog] = useState(false);

  // 🚀 **기존 사용자 정보 불러오기**
  useEffect(() => {
    const fetchUser = async () => {
      try {
        if (numerirUserId === null || isNaN(numerirUserId)) {
          await dialogs.open(CustomDialog, {
            title: 'Notification',
            message: 'Invalid Path.',
            isModal: true
          }, {
            onClose: async () => navigate('/users/user-management', { replace: true }),
          });
          return;
        }

        const { data } = await getUserInfo(numerirUserId); // API에서 기존 데이터 가져오기
        const userInfoData = {
          did: data.did,
          vcSchemaId: data.vcSchemaName,
          userInfo: data.data,
          pii: data.pii
        }
        setFormData(userInfoData);
        setInitialData(userInfoData);
        setSelectedItemId(data.vcSchemaId)
        setIsButtonDisabled(true);
        setIsLoading(false);
      } catch (error) {
        console.error("Failed to fetch user data", error);
        navigate('/error', { state: { message: formatErrorMessage(error, "Failed to retrieve user data.")} });
      } finally {
        setIsLoading(false);
      }
    };


    fetchUser();

  }, [numerirUserId]);

  const handleReset = () => {
    if (initialData) {
      setFormData(initialData);
      setIsButtonDisabled(true);
    }
  };

  const fetchItems = async () => {
    try {
      const response = await fetchVcSchema(0, 10, null, null);
      setAvailableItems(response.data.content || []);
    } catch (error) {
      console.error("Failed to retrieve VC Schemas", error);
    }
  };

  const handleChange = (field: keyof UserFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
    if (formData) {
      setFormData({ ...formData, [field]: event.target.value });
    }
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    const requestBody = {
      id: numerirUserId,
      did: formData.did,
      pii: formData.pii,
      vcSchemaId: selectedItemId,
      userInfo: formData.userInfo,
    };

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to update this user?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      try {
        await patchUserInfo(requestBody);
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Success',
          message: 'User successfully updated.',
          isModal: true,
        });
        navigate('/users/user-management');
      } catch (error) {
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Error',
          message: `Failed to update user: ${error}`,
          isModal: true,
        });
      }
    }
  };

  const handleOpenDialog = () => {
    fetchItems();
    setSelectedItemId(formData?.vcSchemaId || null);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleSelectItem = (item: ItemFormData) => {
    setSelectedItemId(item.id);
  };

  const handleAddSelectedItem = () => {
    if (!selectedItemId) return;
    const selectedItem = availableItems.find((item) => item.id === selectedItemId);
    if (!selectedItem || !formData) return;

    setFormData({ ...formData, vcSchemaId: selectedItem.vcSchemaId });
    handleCloseDialog();
  };

  const validate = () => {
    let tempErrors: ErrorState = {};

    tempErrors.did = validateDid(formData.did);
    tempErrors.vcSchemaId = validateVcSchemaName(formData.vcSchemaId);
    tempErrors.userInfo = validateUserInfo(formData.userInfo);

    setErrors(tempErrors);

    return (
      Object.entries(tempErrors)
        .filter(([key]) => key !== "items" && key !== "errorItemsMessage")
        .every(([, error]) => !error)
    );
  };

  const validateDid = (namespaceId?: string): string | undefined => {
    if (!namespaceId) return 'Please enter a DID.';
    if (namespaceId.length < 4 || namespaceId.length > 64) return 'DID must be between 4 and 64 characters.';
    return undefined;
  };

  const validateVcSchemaName = (name?: string): string | undefined => {
    if (!name) return 'Please enter a VC Schema Name.';
    if (name.length < 4 || name.length > 64) return 'VC Schema Name must be between 4 and 64 characters.';
    return undefined;
  };

  const validateUserInfo = (ref?: string): string | undefined => {
    if (!ref) return 'Please enter a User Info.';
    if (ref.length < 4) return 'User Info must be 4 characters.';
    return undefined;
  };


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

  if (isLoading) return <FullscreenLoader open={true} />;

  return formData ? (
    <>
      <Typography variant="h4">User Management</Typography>
      <StyledContainer>
        <StyledTitle>User Edit</StyledTitle>
        <StyledInputArea>

          <TextField label="DID" fullWidth size="small" margin="normal" value={formData.did} onChange={handleChange('did')} />

          <TextField label="PII" fullWidth size="small" margin="normal" value={formData.pii} onChange={handleChange('pii')} />

          {/* VC Schema 선택 필드 */}
          <Box sx={{ display: "flex", alignItems: "center", gap: 1, mt: 2 }}>
            <TextField label="VC Schema ID" fullWidth size="small" value={formData.vcSchemaId} disabled />
            <IconButton color="primary" onClick={handleOpenDialog}><SearchIcon /></IconButton>
          </Box>
          <Typography variant="h6" sx={{ mt: 3 }}>User VC Info</Typography>
          <TextField
            size="medium"
            multiline
            fullWidth
            value={formData.userInfo}
            onChange={handleChange('userInfo')}
            error={!!errors.userInfo}
            helperText={errors.userInfo}
            placeholder="User Info"
          />

          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            <Button variant="contained" color="primary" disabled={isButtonDisabled} onClick={handleSubmit}>Update</Button>
            <Button variant="contained" color="secondary" onClick={() => navigate(-1)}>Back</Button>
            <Button variant="outlined" color="secondary" onClick={handleReset}>Reset</Button>
          </Box>


          {/* VC Schema 선택 다이얼로그 */}
          <VcSchemaSelectionDialog
            open={openDialog}
            onClose={handleCloseDialog}
            availableItems={availableItems}
            selectedItemId={selectedItemId}
            onSelectItem={handleSelectItem}
            onConfirmSelection={handleAddSelectedItem}
          />
        </StyledInputArea>
      </StyledContainer >
    </>
  ) : null;
};
export default UserEditPage;
