import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';
import { fetchZkpSchemas, postReRegisterSchema } from '../../../apis/zkp_management-api';

type ZkpNamespaceRow = {
  id: number;
  schemaId: string;
  name: string;
  definitionCount: number;
  createdAt: string;
  updatedAt: string;
};

const CredentialSchemaManagementPage = () => {
  const navigate = useNavigate();
  const dialogs = useDialogs();

  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<ZkpNamespaceRow[]>([]);
  const [totalRows, setTotalRows] = useState(0);
  const [selectedRow, setSelectedRow] = useState<string| number | null>(null);
  const [searchText, setSearchText] = useState<string>('');
  const [selectedSearch, setSelectedSearch] = useState<string>('schemaId');

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const selectedRowData = useMemo(
    () => Array.isArray(rows) ? rows.find(row => row.id === selectedRow) || null : null,
    [rows, selectedRow]
  );

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchZkpSchemas(
        paginationModel.page,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.total);
    } catch (err) {
      console.error("Failed to retrieve zkp schemas. ", err);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, "Failed to fetch zkp schema list."),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  const getData = useCallback(async () => {
    setLoading(true);
    try {
      const response = await fetchZkpSchemas(
        0,
        paginationModel.pageSize,
        selectedSearch && searchText.trim() ? selectedSearch : null,
        selectedSearch && searchText.trim() ? searchText.trim() : null
      );
      setRows(response.data.content);
      setTotalRows(response.data.total);
      setPaginationModel((prev) => ({ ...prev, page: 0 }));
    } catch (err) {
      setLoading(false);
      console.error("Failed to retrieve zkp schemas. ", err);
      await dialogs.open(CustomDialog, {
        title: 'Notification',
        message: formatErrorMessage(err, 'Failed to fetch zkp schema list.'),
        isModal: true,
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.pageSize, selectedSearch, searchText, dialogs]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const hansdleReRegisterAll = async () => {
       setLoading(true);
      try {
        await postReRegisterSchema();

        setLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Completed re-register ZKP Credential Schema',
          isModal: true,
        }, {
          onClose: async () => navigate('/zkp-management/credential-schema-management'),
        });
      } catch (error) {
        setLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: formatErrorMessage(error, "Failed to re-register ZKP Credential Schema."),
          isModal: true,
        });
      }
  };

  const handleSearch = useCallback(
    async (field: string, text: string) => {
      const trimmed = text.trim();
      if (!trimmed) return;
      setSelectedSearch(field);
      setSearchText(trimmed);
      setPaginationModel((prev) => ({ ...prev, page: 0 }));
    },
    []
  );

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    width: 1100,
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
        <StyledSubTitle>Credential Schema Management</StyledSubTitle>
        <CustomDataGrid
          rows={rows}
          columns={[
            { field: 'schemaId', headerName: 'ID', width: 200 },
            { field: 'name', headerName: 'Name', width: 100,
              renderCell: (params) => (
                <Link
                  component="button"
                  variant='body2'
                  onClick={() => navigate(`/zkp-management/credential-schema-management/${params.row.id}`)}
                  sx={{ cursor: 'pointer', color: 'primary.main' }}
                >
                  {params.value}
                </Link>
              ),

             },
            { field: 'version', headerName: 'Version', width: 100 },
            { field: 'definitionCount', headerName: 'Definition Count', width: 150 },
            { field: 'status', headerName: 'Status', width: 250 },
            { field: 'createdAt', headerName: 'Registered At', width: 150 },
            { field: 'updatedAt', headerName: 'Updated At', width: 150 },
          ]}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          onRegister={() => navigate('/zkp-management/credential-schema-management/credential-schema-registration')}
          paginationMode="server"
          totalRows={totalRows}
          paginationModel={paginationModel}
          setPaginationModel={setPaginationModel}
          additionalButtons={[
            { label: 'Re-register all', onClick: () => hansdleReRegisterAll(), color: 'primary' },
          ]}
          enableSearch={true}
          searchText={searchText}
          setSearchText={setSearchText}
          selectedSearch={selectedSearch}
          setSelectedSearch={setSelectedSearch}
          searchOptions={[
            { value: 'schemaId', label: 'ID' },
            { value: 'name', label: 'Name' },
          ]}
          onSearch={handleSearch}
          onRefresh={getData}
        />
      </StyledContainer>
    </>
  )
}

export default CredentialSchemaManagementPage
