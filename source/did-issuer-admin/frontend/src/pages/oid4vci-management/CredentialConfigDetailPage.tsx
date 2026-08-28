import { Box, Button, IconButton, TextField, Typography, styled, Alert, useTheme, Divider, Breadcrumbs, Link, Paper, MenuItem } from '@mui/material';
import React, { useEffect, useState, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router';
import { getCredentialConfigs, updateCredentialConfigs, SdkCredentialConfig, getMetadata, updateMetadata, IssuerMetadata } from '../../apis/oid4vci-api';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import FullscreenLoader from '../../components/loading/FullscreenLoader';

const StyledContainer = styled(Box)(({ theme }) => ({
    width: 600,
    margin: 'auto',
    marginTop: theme.spacing(3),
    padding: theme.spacing(4),
    borderRadius: theme.shape.borderRadius,
    backgroundColor: '#ffffff',
    boxShadow: '0px 4px 8px 0px #0000001A',
}));

const StyledTitle = styled(Typography)({
    textAlign: 'left',
    fontSize: '24px',
    fontWeight: 700,
    marginBottom: '24px',
});

const StyledInputArea = styled(Box)(({ theme }) => ({
    marginTop: theme.spacing(2),
}));

const SectionTitle = styled(Typography)(({ theme }) => ({
    fontSize: '18px',
    fontWeight: 700,
    marginTop: theme.spacing(4),
    marginBottom: theme.spacing(2),
    color: theme.palette.primary.main,
}));

const MSO_MDOC_FORMAT = 'mso_mdoc-did';

export default function CredentialConfigDetailPage() {
    const theme = useTheme();
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const isEdit = !!id;

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [configs, setConfigs] = useState<Record<string, SdkCredentialConfig>>({});
    const [metadata, setMetadata] = useState<IssuerMetadata | null>(null);

    const [configId, setConfigId] = useState(id || '');
    const [format, setFormat] = useState(MSO_MDOC_FORMAT);
    const [identifiers, setIdentifiers] = useState<string[]>([]);
    const [metadataJson, setMetadataJson] = useState('');

    useEffect(() => {
        fetchData();
    }, [id]);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [configsRes, metadataRes] = await Promise.all([getCredentialConfigs(), getMetadata()]);
            const allConfigs = configsRes.data;
            const allMetadata = metadataRes.data;
            
            setConfigs(allConfigs);
            setMetadata(allMetadata);

            if (isEdit && id && allConfigs[id]) {
                const config = allConfigs[id];
                setFormat(config.format || '');
                setIdentifiers(config.identifiers || []);
                
                // Fetch from metadata if available, otherwise use stored metadataJson
                const metaConfig = allMetadata.credentialConfigurationsSupported?.[id];
                if (metaConfig) {
                    setMetadataJson(JSON.stringify(metaConfig, null, 2));
                } else {
                    setMetadataJson(config.metadataJson || '{}');
                }
            } else {
                // Initialize default for new registration
                setMetadataJson(JSON.stringify({
                    format: MSO_MDOC_FORMAT
                }, null, 2));
            }
            setError(null);
        } catch (err) {
            console.error('Failed to fetch data:', err);
            setError('Failed to load configuration details.');
        } finally {
            setLoading(false);
        }
    };

    const handleAddIdentifier = () => {
        setIdentifiers([...identifiers, '']);
    };

    const handleRemoveIdentifier = (index: number) => {
        setIdentifiers(identifiers.filter((_, i) => i !== index));
    };

    const handleIdentifierChange = (index: number, value: string) => {
        const newIdentifiers = [...identifiers];
        newIdentifiers[index] = value;
        setIdentifiers(newIdentifiers);
    };

    const handleSave = async () => {
        if (!configId.trim()) {
            setError('ID is required.');
            return;
        }

        if (!isEdit && configs[configId]) {
            setError('This ID already exists.');
            return;
        }

        try {
            setLoading(true);
            let parsedMetadata;
            try {
                parsedMetadata = JSON.parse(metadataJson);
            } catch (e) {
                setError('Invalid Metadata JSON format.');
                setLoading(false);
                return;
            }

            // 1. Prepare SDK Config Map
            const updatedConfigs = { ...configs };
            updatedConfigs[configId] = {
                id: configId,
                format,
                identifiers: identifiers.filter(i => i.trim() !== ''),
                metadataJson: metadataJson
            };

            // 2. Prepare Metadata Config
            const updatedMetadata = { ...metadata } as IssuerMetadata;
            updatedMetadata.credentialConfigurationsSupported = {
                ...updatedMetadata.credentialConfigurationsSupported,
                [configId]: parsedMetadata
            };

            // 3. Update both
            await Promise.all([
                updateCredentialConfigs(updatedConfigs),
                updateMetadata(updatedMetadata)
            ]);

            navigate('/oid4vci-management/credential-config');
        } catch (err) {
            console.error('Failed to save:', err);
            setError('Failed to save configuration.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <FullscreenLoader open={loading} />
            <Typography variant="h4" sx={{ mb: 1 }}>OID4VCI Management</Typography>
            <StyledContainer>
                <StyledTitle>
                    {isEdit ? 'Credential Config Detail' : 'Register New Configuration'}
                </StyledTitle>

                {error && <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>{error}</Alert>}

                <StyledInputArea>
                    <SectionTitle sx={{ mt: 0 }}>Basic Information</SectionTitle>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField
                            label="Configuration ID"
                            fullWidth
                            value={configId}
                            onChange={(e) => setConfigId(e.target.value)}
                            disabled={isEdit}
                            variant="standard"
                            placeholder="e.g. StudentID"
                            helperText="Unique identifier for this configuration"
                        />
                        <TextField
                            select
                            label="Format"
                            fullWidth
                            value={format || MSO_MDOC_FORMAT}
                            onChange={(e) => setFormat(e.target.value)}
                            variant="standard"
                        >
                            <MenuItem value={MSO_MDOC_FORMAT}>{MSO_MDOC_FORMAT}</MenuItem>
                            <MenuItem value="dc+sd-jwt-did">dc+sd-jwt-did</MenuItem>
                            {format && format !== MSO_MDOC_FORMAT && format !== 'dc+sd-jwt-did' && (
                                <MenuItem value={format} style={{ display: 'none' }}>{format}</MenuItem>
                            )}
                        </TextField>
                    </Box>

                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 4, mb: 1 }}>
                        <SectionTitle sx={{ mt: 0, mb: 0 }}>Identifiers (SDK)</SectionTitle>
                        <Button startIcon={<AddIcon />} onClick={handleAddIdentifier} size="small" variant="outlined">
                            Add
                        </Button>
                    </Box>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                        {identifiers.map((identifier, index) => (
                            <Box key={index} sx={{ display: 'flex', gap: 1 }}>
                                <TextField
                                    fullWidth
                                    size="small"
                                    variant="standard"
                                    value={identifier}
                                    onChange={(e) => handleIdentifierChange(index, e.target.value)}
                                    placeholder="Enter identifier"
                                />
                                <IconButton color="error" size="small" onClick={() => handleRemoveIdentifier(index)}>
                                    <DeleteIcon fontSize="small" />
                                </IconButton>
                            </Box>
                        ))}
                    </Box>

                    <SectionTitle>Metadata JSON</SectionTitle>
                    <TextField
                        multiline
                        rows={10}
                        fullWidth
                        value={metadataJson}
                        onChange={(e) => setMetadataJson(e.target.value)}
                        sx={{ fontFamily: 'monospace', fontSize: '13px', bgcolor: '#f8fafc', p: 1, borderRadius: 1 }}
                        variant="outlined"
                    />

                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 5 }}>
                        <Button variant="contained" onClick={handleSave} size="large" sx={{ px: 4 }}>
                            {isEdit ? 'Save' : 'Register'}
                        </Button>
                        <Button variant="outlined" color="primary" onClick={() => navigate('/oid4vci-management/credential-config')} size="large" sx={{ px: 4 }}>
                            Cancel
                        </Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    );
}
