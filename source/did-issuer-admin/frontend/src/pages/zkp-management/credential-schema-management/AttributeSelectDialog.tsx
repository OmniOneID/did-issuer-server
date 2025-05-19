import {
  Box, Button, Checkbox, Dialog, DialogActions, DialogContent, DialogTitle,
  MenuItem, Select, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Typography
} from '@mui/material';
import { DialogProps } from '@toolpad/core/useDialogs';
import React, { useEffect, useState } from 'react';

interface Attribute {
  namespaceId: string;
  label: string;
  type: string;
}

// 인덱스 시그니처가 있는 인터페이스 추가
interface AttributeMap {
  [key: string]: { label: string; type: string; }[];
}

// DialogProps 타입 수정 - 두 번째 제네릭 타입을 Attribute[]로 변경
type AttributeSelectDialogProps = DialogProps<Attribute[], Attribute[]>;

const mockNamespaceOptions = [
  { id: 'org.rso.10001', name: 'org.rso.10001' },
  { id: 'org.rso.10002', name: 'org.rso.10002' },
];

// 인터페이스 적용
const mockAttributes: AttributeMap = {
  'org.rso.10001': [
    { label: 'zkpcity', type: 'String' },
    { label: 'zkpphone', type: 'String' },
  ],
  'org.rso.10002': [
    { label: 'zkpname', type: 'String' },
    { label: 'zkpid', type: 'Number' },
  ]
};

const AttributeSelectDialog: React.FC<AttributeSelectDialogProps> = ({
  open,
  onClose,
  payload
}) => {
  const [namespaceId, setNamespaceId] = useState('');
  const [attributes, setAttributes] = useState<{ label: string; type: string }[]>([]);
  const [selectedMap, setSelectedMap] = useState<Record<string, boolean>>({});

  useEffect(() => {
    if (namespaceId) {
      setAttributes(mockAttributes[namespaceId] ?? []);
      setSelectedMap({});
    }
  }, [namespaceId]);

  const handleToggle = (label: string) => {
    setSelectedMap((prev) => ({ ...prev, [label]: !prev[label] }));
  };

  const handleClose = (event: unknown, reason?: string) => {
    if (reason === 'backdropClick') return;
    onClose([]); // 이제 빈 배열 반환 가능
  };

  const handleAdd = async () => {
    const selected = attributes.filter(attr => selectedMap[attr.label]);
    const addedItems = selected.map(attr => ({
      namespaceId,
      label: attr.label,
      type: attr.type,
    }));

    if (addedItems.length > 0) {
      // 이제 Attribute[] 타입 반환 가능
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
          onChange={(e) => setNamespaceId(e.target.value)}
          fullWidth
          size="small"
          displayEmpty
        >
          <MenuItem value="" disabled>Select a namespace</MenuItem>
          {mockNamespaceOptions.map(ns => (
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