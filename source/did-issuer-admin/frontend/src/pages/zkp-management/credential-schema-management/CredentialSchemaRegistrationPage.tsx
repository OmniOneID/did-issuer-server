import {
  Box, Button, IconButton, Paper, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, TextField, Typography, styled
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router";
import DeleteIcon from "@mui/icons-material/Delete";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import FullscreenLoader from "../../../components/loading/FullscreenLoader";
import CustomDialog from "../../../components/dialog/CustomDialog";
import CustomConfirmDialog from "../../../components/dialog/CustomConfirmDialog";
import AttributeSelectDialog from "./AttributeSelectDialog";
import { postZkpSchema } from "../../../apis/zkp_management-api";
import { useDialogs } from "@toolpad/core";
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  useSortable,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { formatErrorMessage } from "../../../utils/error-handler";

interface Attribute {
  id: number;
  namespaceId: number;
  label: string;
  type: string;
  caption: string;
  sortOrder: number;
  namespaceName: string;
  namespaceIdentifier: string;
  namespaceRef: string;
}

interface FormData {
  name: string;
  version: string;
  tag: string;
  attributes: Attribute[];
}

interface ErrorState {
  name?: string;
  version?: string;
  tag?: string;
  attributes?: string;
}

interface ApiErrorResponse {
  code: string;
  description: string;
}

const SortableRow = ({ attr, index, onRemove }: {
  attr: Attribute;
  index: number;
  onRemove: (index: number) => void;
}) => {
  const { attributes, listeners, setNodeRef, transform, transition } = useSortable({ id: index });
  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };
  
  return (
    <TableRow ref={setNodeRef} style={style}>
      <TableCell>{attr.sortOrder + 1}</TableCell> 
      <TableCell {...attributes} {...listeners}>{attr.namespaceId}</TableCell>
      <TableCell {...attributes} {...listeners}>{attr.caption}</TableCell>
      <TableCell {...attributes} {...listeners}>{attr.label}</TableCell>
      <TableCell {...attributes} {...listeners}>{attr.type}</TableCell>
      <TableCell>
        <IconButton 
          onClick={() => onRemove(index)}
        >
          <DeleteIcon sx={{ color: "#FF8400" }} />
        </IconButton>
      </TableCell>
    </TableRow>
  );
};

const ZkpCredentialSchemaRegistrationPage = () => {
  const dialogs = useDialogs();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<FormData>({
    name: "",
    version: "",
    tag: "",
    attributes: [],
  });

  const [errors, setErrors] = useState<ErrorState>({});
  const [isLoading, setIsLoading] = useState(false);

  const sensors = useSensors(useSensor(PointerSensor));

  const validate = () => {
    const tempErrors: ErrorState = {};
    if (!formData.name.trim()) tempErrors.name = "Name is required.";
    if (!formData.version.trim()) tempErrors.version = "Version is required.";
    if (!formData.tag.trim()) tempErrors.tag = "Tag is required.";
    if (formData.attributes.length === 0) tempErrors.attributes = "At least one attribute is required.";
    setErrors(tempErrors);
    return Object.keys(tempErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;
    
    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Register this Credential Schema?',
      isModal: true,
    });

    console.log('Submitting form data:', formData);

    if (result) {
      setIsLoading(true);
      try {
        await postZkpSchema(formData);
        setIsLoading(false);
        
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Completed register ZKP schema.',
          isModal: true,
        }, {
          onClose: async () => navigate(-1),
        });

      } catch (error: any) {
        setIsLoading(false);
    
        dialogs.open(CustomDialog, {
          title: 'Notification',
          message: formatErrorMessage(error, "Failed to register ZKP schema."),
          isModal: true,
        });
    }
  };
};

const handleOpenAttributeDialog = async () => {
  const result = await dialogs.open(AttributeSelectDialog, []) as any as Attribute[];
  if (result && Array.isArray(result)) {
    const newList = [...formData.attributes];
    result.forEach(attr => {
      const exists = newList.some(a => a.namespaceId === attr.namespaceId && a.label === attr.label);
      if (!exists) {
        newList.push({
          ...attr,
          sortOrder: newList.length,
        });
      }
    });
    setFormData(prev => ({ ...prev, attributes: newList }));
  }
};

  const handleRemoveAttribute = (index: number) => {
    console.log('Removing attribute at index:', index);
    const updated = [...formData.attributes];
    updated.splice(index, 1);
    setFormData(prev => ({ ...prev, attributes: updated }));
  };

  const handleDragEnd = (event: any) => {
    const { active, over } = event;
    if (active.id === over?.id) return;
    setFormData((prev) => {
      const reordered = arrayMove(prev.attributes, active.id, over.id).map((attr, index) => ({
        ...attr,
        sortOrder: index,
      }));
      return { ...prev, attributes: reordered };
    });
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

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <Typography variant="h4">ZKP Credential Schema Registration</Typography>
      <StyledContainer>
        <TextField
          label="Name *"
          fullWidth
          size="small"
          margin="normal"
          sx={{ width: '60%' }}
          value={formData.name}
          onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          error={!!errors.name}
          helperText={errors.name}
        />
        <TextField
          label="Version *"
          fullWidth
          size="small"
          margin="normal"
          sx={{ width: '60%' }}
          value={formData.version}
          onChange={(e) => setFormData({ ...formData, version: e.target.value })}
          error={!!errors.version}
          helperText={errors.version}
        />
        <TextField
          label="Tag *"
          fullWidth
          size="small"
          margin="normal"
          sx={{ width: '60%' }}
          value={formData.tag}
          onChange={(e) => setFormData({ ...formData, tag: e.target.value })}
          error={!!errors.tag}
          helperText={errors.tag}
        />

        <Typography variant="h6" sx={{ mt: 3 }}>Attributes *</Typography>
        {errors.attributes && (
          <Typography color="error" variant="caption" sx={{ mt: 1, display: "block" }}>
            {errors.attributes}
          </Typography>
        )}

        <Button
          variant="contained"
          startIcon={<AddCircleOutlineIcon />}
          sx={{ my: 2 }}
          onClick={handleOpenAttributeDialog}
        >
          Add Attribute
        </Button>

        <TableContainer component={Paper}>
          <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
            <SortableContext
              items={formData.attributes.map((_, index) => index)}
              strategy={verticalListSortingStrategy}
            >
              <Table>
                <TableHead>
                  <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                    <TableCell>Order</TableCell>
                    <TableCell>Namespace ID</TableCell>
                    <TableCell>Attribute Caption</TableCell>
                    <TableCell>Attribute Label</TableCell>
                    <TableCell>Attribute Type</TableCell>
                    <TableCell>Delete</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {formData.attributes.map((attr, index) => (
                    <SortableRow
                      key={index}
                      index={index}
                      attr={attr}
                      onRemove={handleRemoveAttribute}
                    />
                  ))}
                </TableBody>
              </Table>
            </SortableContext>
          </DndContext>
        </TableContainer>

        <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mt: 4 }}>
          <Button variant="contained" onClick={handleSubmit}>Register</Button>
          <Button variant="outlined" onClick={() => navigate(-1)}>Cancel</Button>
        </Box>
      </StyledContainer>
    </>
  );
};

export default ZkpCredentialSchemaRegistrationPage;