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

interface ItemFormData {
  id: string;
  vcSchemaId: string;
  title: string;
}

interface VcSchemaSelectionDialogProps {
  open: boolean;
  onClose: () => void;
  availableItems: ItemFormData[];
  selectedItemId: string | null;
  onSelectItem: (item: ItemFormData) => void; // ✅ ItemFormData 전체 객체 전달하도록 변경
  onConfirmSelection: () => void;
}

const handleOpenVcSchemaDetail = (vcSchemaId: string) => {

  window.open(`/vc-management/vc-schema-management-popup/${vcSchemaId}?isPopup=true`, "vc schema detail", "popup=yes, width=850, height=800");
};

const VcSchemaSelectionDialog: React.FC<VcSchemaSelectionDialogProps> = ({
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
        <DialogTitle sx={{ fontWeight: 700 }}>Select VC Schema</DialogTitle>
        <Box sx={{ height: "1px", backgroundColor: "#BFBFBF", width: "100%", mt: 1 }} />
      </Box>

      <DialogContent sx={{ px: 2 }}>
        <TableContainer component={Paper} sx={{ maxHeight: 400 }}>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                <TableCell width={50}>Select</TableCell>
                <TableCell>ID</TableCell>
                <TableCell>VC Schema ID</TableCell>
                <TableCell>Title</TableCell>
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
                    <Radio checked={selectedItemId === item.id} />
                  </TableCell>
                  <TableCell>{item.id}</TableCell>
                  <TableCell
                    sx={{ color: "blue", textDecoration: "underline", cursor: "pointer" }}
                    onClick={(e) => {
                      e.stopPropagation();
                      handleOpenVcSchemaDetail(item.id)
                    }}>{item.vcSchemaId}</TableCell>
                  <TableCell>{item.title}</TableCell>
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

export default VcSchemaSelectionDialog;
