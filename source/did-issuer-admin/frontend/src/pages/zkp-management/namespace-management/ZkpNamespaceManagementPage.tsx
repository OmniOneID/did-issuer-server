import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchZkpNamespaces, deleteNamespace } from '../../../apis/zkp_management-api';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';

type ZkpNamespaceRow = {
  id: number;
  namespaceId: string;
  name: string;
  schemaCount: number;
  createdAt: string;
  updatedAt: string;
};

const ZkpNamespaceManagementPage = () => {
  const navigate = useNavigate();
  const dialogs = useDialogs();

  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<ZkpNamespaceRow[]>([]);
  const [totalRows, setTotalRows] = useState(0);
  const [selectedRow, setSelectedRow] = useState<string| number | null>(null);

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const selectedRowData = useMemo(
    () => rows.find(row => row.id === selectedRow) || null,
    [rows, selectedRow]
  );

  const handleDelete = async () => {
    if (!selectedRowData) return;

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to delete this ZKP Namespace?',
      isModal: true,
    });

    if (result) {
      setLoading(true);
      
    }
  };

  useEffect(() => {
    setLoading(true);

    fetchZkpNamespaces(paginationModel.page, paginationModel.pageSize, null, null)
    .then((response) => {
      setLoading(false)
        setRows(response.data.content);
        setTotalRows(response.data.totalElements);
    })
    .catch((err) => {
      setLoading(false)
      console.error("Failed to retrieve zkp namespaces. ", err);
      dialogs.open(CustomDialog, {
          title: 'Notification',
          message: formatErrorMessage(err, "Failed to fetch zkp namespace list."),
          isModal: true,
      });
    });
  }, [paginationModel]);

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    margin: 'auto',
    marginTop: theme.spacing(1),
    padding: theme.spacing(3),
    backgroundColor: '#ffffff',
    borderRadius: theme.shape.borderRadius,
    boxShadow: '0px 4px 8px rgba(0,0,0,0.1)',
  })), []);

  const StyledSubTitle = useMemo(() => styled(Typography)({
    fontSize: '24px',
    fontWeight: 700,
    textAlign: 'left',
  }), []);

  return (
    <>
      <FullscreenLoader open={loading} />
      <StyledContainer>
        <StyledSubTitle>ZKP Namespace Management</StyledSubTitle>
        <CustomDataGrid
          rows={rows}
          columns={[
            { field: 'namespaceId', headerName: 'ID', width: 240 },
            { field: 'name', headerName: 'Name', width: 200 },
            { field: 'schemaCount', headerName: 'Schema Count', width: 150 },
            { field: 'createdAt', headerName: 'Registered At', width: 200 },
            { field: 'updatedAt', headerName: 'Updated At', width: 200 },
          ]}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          onEdit={() => {
            if (selectedRowData?.schemaCount === 0) {
              navigate(`/zkp-management/namespace-edit/${selectedRowData.id}`);
            }
          }}
          onRegister={() => navigate('/zkp-management/zkp-namespace-management/namespace-registration')}
          onDelete={handleDelete}
          paginationMode="server"
          totalRows={totalRows}
          paginationModel={paginationModel}
          setPaginationModel={setPaginationModel}
        />
      </StyledContainer>
    </>
  );
};

export default ZkpNamespaceManagementPage;
