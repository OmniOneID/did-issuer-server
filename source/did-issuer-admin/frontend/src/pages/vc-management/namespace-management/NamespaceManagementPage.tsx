import { Link } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { fetchNamepsaces } from '../../../apis/vc-management-api';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import { useMemo } from 'react';

type Props = {}

const statusMapping: { [key: string]: string } = {
  ACTIVATE: "ACTIVATE",
  DEACTIVATE: "DEACTIVATE",
  REQUIRED_ENROLL_ENTITY: "REQUIRED_ENROLL_ENTITY",
};

type NamespaceRow = {
  id: string | number;
  namespaceId: string;
  name: string;
  createdAt: string;
};

const NamespaceManagementPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState<boolean>(false);
  // const [rows, setRows] = useState<{ id: string | number }[]>([]);
  const [totalRows, setTotalRows] = useState<number>(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
  const [rows, setRows] = useState<NamespaceRow[]>([]);

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 1,
    pageSize: 10,
  });

  const selectedRowData = useMemo(() => {
    return rows.find(row => row.id === selectedRow) || null;
  }, [rows, selectedRow]);
  
  useEffect(() => {
    setLoading(true);
    fetchNamepsaces(paginationModel.page - 1, paginationModel.pageSize, null, null)
    .then((response) => {
      setRows(response.data.content);
      setTotalRows(response.data.totalElements);
    })
    .catch((error) => {
      console.error("Failed to retrieve namespaces. ", error)
      navigate('/error', { state: { message: `Failed to retrieve Namepsaces: ${error}` } })
    })
    .finally(() => setLoading(false));
  }, []);

  return (
    <>
      <FullscreenLoader open={loading} />

      <CustomDataGrid 
          rows={rows} 
          columns={[
            { field: 'namespaceId', headerName: "ID", width: 200},
            { 
              field: 'name', 
              headerName: "Name", 
              width: 200,
              renderCell: (params) => (
                <Link 
                  component="button"
                  variant='body2'
                  onClick={() => navigate(`/entities/entity-management/${params.row.id}`)}
                  sx={{ cursor: 'pointer', color: 'primary.main' }}
                >
                  {params.value}
                </Link>),
            },
            { field: 'createdAt', headerName: "Registered At", width: 200},
          ]} 
          selectedRow={selectedRow} 
          setSelectedRow={setSelectedRow}
          onEdit={() => {
            if (selectedRowData) {
              navigate(`/vc-management/namespace-management/namespace-edit/${selectedRowData.id}`);
            }
          }}
          onRegister={() => navigate('/vc-management/namespace-management/namespace-registration')}
          additionalButtons={[
           
          ]}
          paginationMode="server" 
          totalRows={totalRows} 
          paginationModel={paginationModel} 
          setPaginationModel={setPaginationModel} 
        />
    </>
  )
}

export default NamespaceManagementPage