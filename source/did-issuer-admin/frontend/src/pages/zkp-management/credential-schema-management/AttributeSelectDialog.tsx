import {
  Box, Button, Checkbox, Dialog, DialogActions, DialogContent, DialogTitle,
  MenuItem, Select, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Typography
} from '@mui/material';
import { DialogProps } from '@toolpad/core/useDialogs';
import React, { useEffect, useState } from 'react';
import { getZkpNamespaceAll, getZkpAttributes } from '../../../apis/zkp_management-api';

interface Attribute {
  namespaceId: string;
  label: string;
  type: string;
}

interface AttributeMap {
  [key: string]: { label: string; type: string; }[];
}

type AttributeSelectDialogProps = DialogProps<Attribute[], Attribute[]>;

const AttributeSelectDialog: React.FC<AttributeSelectDialogProps> = ({
  open,
  onClose,
  payload
}) => {
  const [namespaceId, setNamespaceId] = useState<number | ''>('');
  const [namespaceOptions, setNamespaceOptions] = useState<{ id: string; name: string }[]>([]);
  const [attributes, setAttributes] = useState<{ label: string; type: string }[]>([]);
  const [selectedMap, setSelectedMap] = useState<Record<string, boolean>>({});

  useEffect(() => {
    const fetchNamespaces = async () => {
      try {
        const res = await getZkpNamespaceAll(); // API 호출
        const options = res.data.map((ns: any) => ({
          id: ns.id,
          name: ns.name,
        }));
        setNamespaceOptions(options);
      } catch (error) {
        console.error('Failed to fetch namespaces:', error);
      }
    };

    fetchNamespaces();
  }, []);

  useEffect(() => {
    const fetchAttributes = async () => {
      if (!namespaceId) return;
      try {
        const res = await getZkpAttributes(namespaceId); // API 호출
        console.log('Fetched attributes:', res.data);
        const attrs = res.data.map((item: any) => ({
          label: item.label,
          type: item.type,
        }));
        setAttributes(attrs);
        setSelectedMap({});
      } catch (error) {
        console.error(`Failed to fetch attributes for namespace ${namespaceId}:`, error);
        setAttributes([]);
      }
    };

    fetchAttributes();
  }, [namespaceId]);

  const handleToggle = (label: string) => {
    setSelectedMap((prev) => ({ ...prev, [label]: !prev[label] }));
  };

  const handleClose = (event: unknown, reason?: string) => {
    if (reason === 'backdropClick') return;
    onClose([]);
  };

  const handleAdd = async () => {
    const selected = attributes.filter(attr => selectedMap[attr.label]);
    const addedItems = selected.map(attr => ({
      namespaceId: namespaceId.toString(),
      label: attr.label,
      type: attr.type,
    }));

    if (addedItems.length > 0) {
      await onClose(addedItems);
    } else {
      await onClose([]);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      disableEscapeKeyDown
      fullWidth
      sx={{ maxWidth: 600, margin: '0 auto' }}
    >
      <Box sx={{ px: 2 }}>
        <DialogTitle sx={{ p: 0, pt: 2, fontWeight: 700 }}>Select Attributes</DialogTitle>
        <Box sx={{ height: '1px', backgroundColor: '#BFBFBF', width: '100%', mt: 1 }} />
      </Box>

      <DialogContent sx={{ px: 2 }}>
        <Typography sx={{ mt: 2, mb: 1 }}>Namespace</Typography>
        <Select
          value={namespaceId}
          onChange={(e) => setNamespaceId(Number(e.target.value))}
          fullWidth
          size="small"
          displayEmpty
        >
          <MenuItem value="" disabled>Select a namespace</MenuItem>
          {namespaceOptions.map(ns => (
            <MenuItem key={ns.id} value={ns.id}>{ns.name}</MenuItem>
          ))}
        </Select>

        <Typography sx={{ mt: 3, mb: 1 }}>Attributes</Typography>
        <TableContainer sx={{ border: '1px solid #ddd' }}>
          <Table size="small">
            <TableHead sx={{ backgroundColor: "#f5f5f5" }}>
              <TableRow>
                <TableCell>Select</TableCell>
                <TableCell>Label</TableCell>
                <TableCell>Type</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {attributes.map(attr => (
                <TableRow key={attr.label}>
                  <TableCell>
                    <Checkbox
                      checked={!!selectedMap[attr.label]}
                      onChange={() => handleToggle(attr.label)}
                    />
                  </TableCell>
                  <TableCell>{attr.label}</TableCell>
                  <TableCell>{attr.type}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </DialogContent>

      <DialogActions sx={{ px: 2, pt: 0, display: 'flex', justifyContent: 'center', mt: 2, mb: 2 }}>
        <Button
          variant="outlined"
          onClick={() => onClose([])}
          sx={{ width: '40%', height: '44px', mr: 2 }}
        >
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={handleAdd}
          disabled={!namespaceId}
          sx={{ width: '40%', height: '44px' }}
        >
          Add
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AttributeSelectDialog;