import SearchIcon from "@mui/icons-material/Search";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import RemoveCircleOutlineIcon from "@mui/icons-material/RemoveCircleOutline";
import { Box, Button, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, useTheme, Dialog, DialogTitle, DialogContent, DialogActions, Radio, Select, MenuItem, FormControl, InputLabel } from "@mui/material";
import { useState } from "react";
import { useNavigate } from "react-router";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import { fetchNamespaces, fetchVcSchema, postIssueProfile } from "../../../apis/vc-management-api";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import { useDialogs } from "@toolpad/core/useDialogs";
import CustomDialog from "../../../components/dialog/CustomDialog";
import { Language } from "@mui/icons-material";

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
}

interface ItemFormData {
  id: string;
  vcSchemaId: string;
  title: string;
}

const cipherOptions = ["AES"];
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
  });

  const [isLoading, setIsLoading] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);
  const [availableItems, setAvailableItems] = useState<ItemFormData[]>([]);
  const [selectedItemId, setSelectedItemId] = useState<string | null>(null);


  // 서버로 데이터 전송
  const handleSubmit = async () => {
    const requestBody = {
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
    };

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register Issue Profile?',
      isModal: true,
    });
    if (result) {
      setIsLoading(true);
      try {
        await postIssueProfile(requestBody);
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
  const handleOpenVcSchemaDetail = (vcSchemaId: string) => {
    
    window.open(`/vc-management/vc-schema-management-popup/${vcSchemaId}`, "vc schema detail", "popup=yes, width=800, height=650");
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

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Box sx={{ p: 3 }}>
        <Typography variant="h4">Issue Profile Registration</Typography>

        <Box sx={{ maxWidth: 800, margin: 'auto', mt: 2, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
          <TextField label="VC Plan ID" fullWidth margin="normal" size="small" value={formData.vcPlanId} onChange={(e) => setFormData({ ...formData, vcPlanId: e.target.value })} />
          <TextField label="Title" fullWidth margin="normal" size="small" value={formData.title} onChange={(e) => setFormData({ ...formData, title: e.target.value })} />
          <TextField label="Description" fullWidth margin="normal" size="small" value={formData.description} onChange={(e) => setFormData({ ...formData, description: e.target.value })} />

          {/* VC Schema ID 입력 필드 + 찾기 버튼 */}
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <TextField label="VC Schema ID" fullWidth margin="normal" size="small" value={formData.vcSchemaId} disabled />
            <IconButton color="primary" onClick={handleOpenDialog}><SearchIcon /></IconButton>
          </Box>

          <FormControl fullWidth size="small" sx={{ maxWidth: 800, margin: 'auto', mt: 2, }}>
              <InputLabel>Initiate Type</InputLabel>
              <Select label="Initiate Type" value={formData.initiateType} onChange={handleSelectChange("initiateType")}>
                {initiateTypeOptions.map((option) => <MenuItem key={option.key} value={option.value}> {option.key}</MenuItem>)}
              </Select>
          </FormControl>
          <TextField label="Language" fullWidth margin="normal"  size="small" value={formData.language} onChange={(e) => setFormData({ ...formData, language: e.target.value })} />
          {/* Endpoints 입력 필드 */}
          <Typography variant="h6" sx={{ mt: 3 }}>Endpoints</Typography>
          {formData.endpoints.map((endpoint, index) => (
            <Box key={index} sx={{ display: "flex", alignItems: "center", gap: 1, mt: 1 }}>
              <TextField fullWidth size="small" value={endpoint} onChange={(e) => handleChangeEndpoint(index, e.target.value)} />
              <IconButton color="error" onClick={() => handleRemoveEndpoint(index)}><RemoveCircleOutlineIcon /></IconButton>
            </Box>
          ))}
          <Button startIcon={<AddCircleOutlineIcon />} sx={{ mt: 1 }} onClick={handleAddEndpoint}>Add Endpoint</Button>

          <Typography variant="h6" sx={{ mt: 3 }}>Endpoints</Typography>
          <FormControl fullWidth size="small" sx={{  margin: 'auto', mt: 2, }}>
            <InputLabel>Cipher</InputLabel>
            <Select label="Cipher" value={formData.cipher} onChange={handleSelectChange("cipher")}>
              {cipherOptions.map((option) => <MenuItem key={option} value={option}>{option}</MenuItem>)}
            </Select>
          </FormControl>

          <FormControl fullWidth size="small" sx={{  margin: 'auto', mt: 2, }}>
            <InputLabel>Curve</InputLabel>
            <Select label="Curve" value={formData.curve} onChange={handleSelectChange("curve")}>
              {curveOptions.map((option) => <MenuItem key={option} value={option}>{option}</MenuItem>)}
            </Select>
          </FormControl>

          <FormControl fullWidth size="small" sx={{  margin: 'auto', mt: 2, }}>
            <InputLabel>Padding</InputLabel>
            <Select label="Padding" value={formData.padding} onChange={handleSelectChange("padding")}>
              {paddingOptions.map((option) => <MenuItem key={option} value={option}>{option}</MenuItem>)}
            </Select>
          </FormControl>




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

          <Button variant="contained" color="primary" onClick={handleSubmit} sx={{ mt: 3 }}>Register</Button>
        </Box>
      </Box >
    </>
  );
}

export default IssueProfileRegistrationPage;