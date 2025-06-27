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
  Checkbox,
} from "@mui/material";

interface ItemFormData {
  id: string;
  namespaceId: string;
  name: string;
}

interface CustomItemSelectionDialogProps {
  open: boolean;
  onClose: () => void;
  availableItems: ItemFormData[];
  selectedItems: string[];
  onSelectItem: (id: string) => void;
  onConfirmSelection: () => void;
}

const CustomItemSelectionDialog: React.FC<CustomItemSelectionDialogProps> = ({
  open,
  onClose,
  availableItems,
  selectedItems,
  onSelectItem,
  onConfirmSelection,
}) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" sx={{ maxWidth: 700, margin: "0 auto" }}>
      <Box sx={{ px: 2 }}>
        <DialogTitle sx={{ fontWeight: 700 }}>Select Items</DialogTitle>
        <Box sx={{ height: "1px", backgroundColor: "#BFBFBF", width: "100%", mt: 1 }} />
      </Box>

      <DialogContent sx={{ px: 2 }}>
        <TableContainer component={Paper} sx={{ maxHeight: 400 }}>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                <TableCell width={50}>Select</TableCell>
                <TableCell>ID</TableCell>
                <TableCell>Namespace ID</TableCell>
                <TableCell>Name</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {availableItems.map((item) => (
                <TableRow key={item.id} hover onClick={() => onSelectItem(item.id)} sx={{ cursor: "pointer" }}>
                  <TableCell>
                    <Checkbox checked={selectedItems.includes(item.id)} />
                  </TableCell>
                  <TableCell>{item.id}</TableCell>
                  <TableCell sx={{ color: "blue", textDecoration: "underline", cursor: "pointer" }}>
                    {item.namespaceId}
                  </TableCell>
                  <TableCell>{item.name}</TableCell>
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
          Add Items
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CustomItemSelectionDialog;
