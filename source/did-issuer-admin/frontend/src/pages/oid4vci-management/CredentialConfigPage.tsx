import { Box, Button, Card, CardContent, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, styled, Alert, IconButton, useTheme } from '@mui/material';
import React, { useEffect, useState, useMemo, useCallback } from 'react';
import { useNavigate } from 'react-router';
import { getCredentialConfigs, updateCredentialConfigs, SdkCredentialConfig, getMetadata, updateMetadata, IssuerMetadata } from '../../apis/oid4vci-api';
import AddIcon from '@mui/icons-material/Add';
import RefreshIcon from '@mui/icons-material/Refresh';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { useDialogs } from '@toolpad/core/useDialogs';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';

export default function CredentialConfigPage() {
    const theme = useTheme();
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [configs, setConfigs] = useState<Record<string, SdkCredentialConfig>>({});
    const [metadata, setMetadata] = useState<IssuerMetadata | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    
    // Data Grid state
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [searchText, setSearchText] = useState<string>('');
    const [selectedSearch, setSelectedSearch] = useState<string>('id');
    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
        margin: 'auto',
        marginTop: theme.spacing(1),
        padding: theme.spacing(3),
        border: 'none',
        borderRadius: theme.shape.borderRadius,
        backgroundColor: '#ffffff',
        boxShadow: '0px 4px 8px 0px #0000001A',
    })), []);

    const StyledTitle = useMemo(() => styled(Typography)({
        textAlign: 'left',
        fontSize: '24px',
        fontWeight: 700,
        marginBottom: '16px',
    }), []);

    const rows = useMemo(() => {
        return Object.entries(configs).map(([id, config]) => ({
            ...config,
            id
        }));
    }, [configs]);

    const columns: GridColDef[] = [
        { 
            field: 'id', 
            headerName: 'ID', 
            width: 200,
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', height: '100%' }}>
                    <Typography
                        variant="body2"
                        sx={{
                            color: theme.palette.primary.main,
                            textDecoration: 'underline',
                            cursor: 'pointer',
                            fontWeight: 600
                        }}
                        onClick={() => handleOpenRegister(params.value as string)}
                    >
                        {params.value}
                    </Typography>
                </Box>
            )
        },
        { field: 'format', headerName: 'Format', width: 150 },
        { 
            field: 'identifiers', 
            headerName: 'Identifiers', 
            width: 250,
            renderCell: (params) => (params.value as string[])?.join(', ') || ''
        },
        { 
            field: 'createdAt', 
            headerName: 'Registered Date', 
            width: 180,
            valueGetter: () => '2025-01-01 09:00:00' // Placeholder as per design
        },
        { 
            field: 'updatedAt', 
            headerName: 'Modified Date', 
            width: 180,
            valueGetter: () => '2025-01-01 09:00:00' // Placeholder as per design
        },
    ];

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [configsRes, metadataRes] = await Promise.all([getCredentialConfigs(), getMetadata()]);
            setConfigs(configsRes.data);
            setMetadata(metadataRes.data);
            setError(null);
        } catch (err) {
            console.error('Failed to fetch data:', err);
            setError('Failed to load credential configurations.');
        } finally {
            setLoading(false);
        }
    };

    const handleOpenRegister = (id = '') => {
        if (id) {
            navigate(`/oid4vci-management/credential-config/${id}`);
        } else {
            navigate('/oid4vci-management/credential-config/registration');
        }
    };

    const handleEdit = () => {
        if (selectedRow) {
            handleOpenRegister(selectedRow as string);
        }
    };

    // handleSave removed as it's moved to CredentialConfigDetailPage.tsx

    const handleDelete = async () => {
        if (!selectedRow || !metadata) return;

        const confirmed = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: `Are you sure you want to delete ${selectedRow}?`,
            isModal: true,
        });

        if (!confirmed) return;

        try {
            setLoading(true);
            const id = selectedRow as string;
            const newConfigs = { ...configs };
            delete newConfigs[id];

            const newMetadata = { ...metadata };
            const newMetaConfigs = { ...newMetadata.credentialConfigurationsSupported };
            delete newMetaConfigs[id];
            newMetadata.credentialConfigurationsSupported = newMetaConfigs;

            await Promise.all([
                updateCredentialConfigs(newConfigs),
                updateMetadata(newMetadata)
            ]);

            setSelectedRow(null);
            fetchData();
        } catch (err) {
            console.error('Failed to delete config:', err);
            setError('Failed to delete configuration.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <FullscreenLoader open={loading} />
            <StyledContainer>
                <Typography variant="overline" color="primary" sx={{ fontWeight: 700 }}>
                    OID4VCI Management
                </Typography>
                <StyledTitle>Credential Configurations</StyledTitle>

                {error && <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>{error}</Alert>}

                <CustomDataGrid
                    rows={rows}
                    columns={columns}
                    selectedRow={selectedRow}
                    setSelectedRow={setSelectedRow}
                    paginationMode="client"
                    searchText={searchText}
                    setSearchText={setSearchText}
                    selectedSearch={selectedSearch}
                    setSelectedSearch={setSelectedSearch}
                    onRegister={() => handleOpenRegister()}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    onRefresh={fetchData}
                />

            </StyledContainer>
        </>
    );
}
