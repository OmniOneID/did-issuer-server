import SearchIcon from "@mui/icons-material/Search";
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, MenuItem, Paper, Radio, Select, SelectChangeEvent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme } from "@mui/material";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import FullscreenLoader from "../../components/loading/FullscreenLoader";
import { postUserInfo } from "../../apis/admin-api";
import { useDialogs } from "@toolpad/core";
import CustomConfirmDialog from "../../components/dialog/CustomConfirmDialog";
import CustomDialog from "../../components/dialog/CustomDialog";
import { fetchVcSchema } from "../../apis/vc-management-api";

type Props = {}

interface UserFormData {
  did: string;
  vcSchemaId: string;
  firstName: string;
  lastName: string;
  userInfo: string;
}

interface ErrorState {
  did?: string;
  vcSchemaId?: string;
  firstName?: string;
  lastName?: string;
  userInfo?: string;
}

interface ItemFormData {
  id: string;
  vcSchemaId: string;
  title: string;
}

const UserRegistrationPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const theme = useTheme();

  const [formData, setFormData] = useState<UserFormData>({
      did: '',
      vcSchemaId: '',
      firstName: '',
      lastName: '',
      userInfo: '',
  });
  

  const [errors, setErrors] = useState<ErrorState>({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedItemId, setSelectedItemId] = useState<string | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [availableItems, setAvailableItems] = useState<ItemFormData[]>([]);

  const handleChange = (field: keyof UserFormData) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value;
    setFormData((prev) => ({ ...prev, [field]: newValue }));
  };

  const validate = () => {
    let tempErrors: ErrorState = {};

    tempErrors.did = validateDid(formData.did);
    tempErrors.vcSchemaId = validateVcSchemaName(formData.vcSchemaId);
    tempErrors.firstName = validateFirstName(formData.firstName);
    tempErrors.lastName = validateLastName(formData.lastName);
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

  const validateFirstName = (ref?: string): string | undefined => {
    if (!ref) return 'Please enter a First Name.';
    if (ref.length < 4 || ref.length > 64) return 'First Name must be between 4 and 64 characters.';
    return undefined;
  };
  const validateLastName = (ref?: string): string | undefined => {
    if (!ref) return 'Please enter a Last Name.';
    if (ref.length < 4 || ref.length > 64) return 'Last Name must be between 4 and 64 characters.';
    return undefined;
  };
  const validateUserInfo = (ref?: string): string | undefined => {
    if (!ref) return 'Please enter a User Info.';
    if (ref.length < 4) return 'User Info must be 4 characters.';
    return undefined;
  };


  const handleSubmit = async () => {
    if (!validate()) return;

    const requestBody = {
      did: formData.did,
      vcSchemaId: selectedItemId,
      firstName: formData.firstName,
      lastName: formData.lastName,
      userInfo: formData.userInfo,
    };

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register User?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      try {
        await postUserInfo(requestBody);
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Completed register User.',
          isModal: true,
        }, {
          onClose: async (result) => navigate('/users/user-management'),
        });
      } catch (error) {
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: `Failed to register User: ${error}`,
          isModal: true,
        });
      }
    };
  }

  const handleReset = () => {
    setErrors({});
    setIsButtonDisabled(true);
    setFormData({ did: '', vcSchemaId: '', firstName: '', lastName: '', userInfo: '' });
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
  
  const handleOpenVcSchemaDetail = (vcSchemaId: string) => {
    
    window.open(`/vc-management/vc-schema-management-popup/${vcSchemaId}`, "vc schema detail", "popup=yes, width=800, height=650");
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
          <TextField
            label="DID"
            variant="outlined"
            margin="normal"
            size="small"
            fullWidth
            value={formData.did}
            onChange={handleChange('did')}
            error={!!errors.did}
            helperText={errors.did}
          />

          {/* VC Schema ID 입력 필드 + 찾기 버튼 */}
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <TextField 
              label="VC Schema ID" 
              fullWidth 
              margin="normal" 
              size="small" 
              value={formData.vcSchemaId} 
              disabled 
            />
            <IconButton color="primary" onClick={handleOpenDialog}><SearchIcon /></IconButton>
          </Box>

          <TextField
            label="FirstName"
            variant="outlined"
            margin="normal"
            size="small"
            fullWidth
            value={formData.firstName}
            onChange={handleChange('firstName')}
            error={!!errors.firstName}
            helperText={errors.firstName}
          />

          <TextField
            label="LastName"
            variant="outlined"
            margin="normal"
            size="small"
            fullWidth
            value={formData.lastName}
            onChange={handleChange('lastName')}
            error={!!errors.lastName}
            helperText={errors.lastName}
          />


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



          {/* 다이얼로그 - VC Schema 선택 */}
          <Dialog open={openDialog} onClose={handleCloseDialog} fullWidth maxWidth="md">
            <DialogTitle>Select VC Schema</DialogTitle>
            <DialogContent>
              <TableContainer component={Paper}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Select</TableCell>
                      <TableCell>ID</TableCell>
                      <TableCell>VC Schema ID</TableCell>
                      <TableCell>Title</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {availableItems.map((item) => (
                      <TableRow key={item.id} hover onClick={() => handleSelectItem(item)} sx={{ cursor: "pointer" }}>
                        <TableCell>
                          <Radio checked={selectedItemId === item.id} />
                        </TableCell>
                        <TableCell>{item.id}</TableCell>
                        <TableCell 
                          sx={{ color: "blue", textDecoration: "underline", cursor: "pointer" }}
                          onClick={(e) => {
                            e.stopPropagation();
                            handleOpenVcSchemaDetail(item.id)}}>{item.vcSchemaId}</TableCell>
                        <TableCell>{item.title}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </DialogContent>

            <DialogActions>
              <Button onClick={handleCloseDialog}>Cancel</Button>
              <Button onClick={handleAddSelectedItem} variant="contained">Select</Button>
            </DialogActions>
          </Dialog>


          <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
            <Button variant="contained" color="secondary" onClick={() => navigate('/user-management')}>
              Back
            </Button>
            <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>
            <Button variant="contained" color="primary" onClick={handleSubmit} disabled={isButtonDisabled}>Register</Button>
          </Box>
        </Box>
      </Box>
    </>
  );

}

export default UserRegistrationPage;
