import React from "react";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Box,
  Button,
  TableContainer,
  Paper,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Radio,
} from "@mui/material";

interface ZkpItemFormData {
  id: string;
  definitionId: string;
  schemaId: string;
  version: string;
  tag: string;
}

interface CredentialDefinitionSelectionDialogProps {
  open: boolean;
  onClose: () => void;
  availableItems: ZkpItemFormData[];
  selectedItemId: string | null;
  onSelectItem: (item: ZkpItemFormData) => void; // ✅ ItemFormData 전체 객체 전달하도록 변경
  onConfirmSelection: () => void;
}

const handleOpenCredentialDefinitionDetail = (definitionId: string) => {

  window.open(`/vc-management/vc-schema-management-popup/${definitionId}?isPopup=true`, "vc schema detail", "popup=yes, width=850, height=800");
};

const CredentialDefinitionSelectionDialog: React.FC<CredentialDefinitionSelectionDialogProps> = ({
  open,
  onClose,
  availableItems,
  selectedItemId,
  onSelectItem,
  onConfirmSelection,
}) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" sx={{ maxWidth: 700, margin: "0 auto" }}>
      <Box sx={{ px: 2 }}>
        <DialogTitle sx={{ fontWeight: 700 }}>Select Credential Definition</DialogTitle>
        <Box sx={{ height: "1px", backgroundColor: "#BFBFBF", width: "100%", mt: 1 }} />
      </Box>

      <DialogContent sx={{ px: 2 }}>
        <TableContainer component={Paper} sx={{ maxHeight: 400 }}>
          <Table stickyHeader>
            <TableHead>
              {/* interface ZkpItemFormData {
  id: string;
  definitionId: string;
  schemaId: string;
  version: string;
  tag: string;
} */}
              <TableRow>
                <TableCell width={50}>Select</TableCell>
                <TableCell>ID</TableCell>
                <TableCell>Definition ID</TableCell>
                <TableCell>Schema ID</TableCell>
                <TableCell>Definition Version</TableCell>
                <TableCell>Tag</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {availableItems.map((item) => (
                <TableRow
                  key={item.id}
                  hover
                  onClick={() => onSelectItem(item)}
                  sx={{ cursor: "pointer" }}
                >
                  <TableCell>
                    <Radio checked={selectedItemId === item.definitionId} />
                  </TableCell>
                  <TableCell>{item.id}</TableCell>
                  <TableCell>{item.definitionId}</TableCell>
                  <TableCell>{item.schemaId}</TableCell>
                  <TableCell>{item.version}</TableCell>
                  <TableCell>{item.tag}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </DialogContent>

      <DialogActions sx={{ px: 2, pt: 0, display: "flex", gap: 2, mt: 0 }}>
        <Button variant="outlined" onClick={onClose} color="primary" sx={{ flexGrow: 1, height: "48px" }}>
          Cancel
        </Button>
        <Button variant="contained" onClick={onConfirmSelection} color="primary" autoFocus sx={{ flexGrow: 1, height: "48px" }}>
          Select
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CredentialDefinitionSelectionDialog;
