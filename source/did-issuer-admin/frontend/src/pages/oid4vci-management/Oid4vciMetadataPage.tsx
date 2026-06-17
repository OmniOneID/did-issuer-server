import { Box, Button, Card, CardContent, Divider, Grid, List, ListItem, ListItemText, Switch, TextField, Typography, styled, IconButton, Alert, CircularProgress, useTheme, Paper } from '@mui/material';
import React, { useEffect, useState, useMemo } from 'react';
import { getMetadata, updateMetadata, IssuerMetadata, DEFAULT_METADATA } from '../../apis/oid4vci-api';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import FullscreenLoader from '../../components/loading/FullscreenLoader';

export default function Oid4vciMetadataPage() {
    const theme = useTheme();
    const [metadata, setMetadata] = useState<IssuerMetadata>(DEFAULT_METADATA);
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

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

    const SectionHeader = useMemo(() => styled(Box)(({ theme }) => ({
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: theme.spacing(3),
    })), []);

    const FieldLabel = useMemo(() => styled(Typography)(({ theme }) => ({
        fontWeight: 700,
        fontSize: '14px',
        color: theme.palette.text.secondary,
        marginBottom: theme.spacing(1),
    })), []);

    const SectionPaper = useMemo(() => styled(Paper)(({ theme }) => ({
        padding: theme.spacing(2),
        backgroundColor: '#f8fafc',
        borderRadius: theme.shape.borderRadius,
        border: '1px solid #e2e8f0',
    })), []);

    useEffect(() => {
        fetchMetadata();
    }, []);

    const fetchMetadata = async () => {
        try {
            setLoading(true);
            const response = await getMetadata();
            const data = response.data as any;

            const credentialIssuer = data.credentialIssuer || data.credential_issuer || '';
            const base = credentialIssuer.replace(/\/$/, '');

            // Mapping/Migration logic for both legacy snake_case and even older flat structure
            const mappedData: IssuerMetadata = {
                credentialIssuer: credentialIssuer,
                authorizationServer: data.authorizationServer || data.authorization_server || [''],
                credentialOfferEndpoint: `${base}/credential-offer`,
                credentialEndpoint: `${base}/credential`,
                nonceEndpoint: `${base}/nonce`,
                deferredCredentialEndpoint: `${base}/deferred_credential`,
                notificationEndpoint: `${base}/notification`,
                credentialConfigurationsSupported: data.credentialConfigurationsSupported || data.credential_configurations_supported || {},
                credentialRequestEncryption: data.credentialRequestEncryption || data.credential_request_encryption || DEFAULT_METADATA.credentialRequestEncryption,
                credentialResponseEncryption: data.credentialResponseEncryption || data.credential_response_encryption || DEFAULT_METADATA.credentialResponseEncryption,
            };

            // Deep mapping for request encryption
            if (data.credential_request_encryption || data.credentialRequestEncryption) {
                const reqEnc = data.credentialRequestEncryption || data.credential_request_encryption;
                mappedData.credentialRequestEncryption = {
                    encValuesSupported: reqEnc.encValuesSupported || reqEnc.enc_values_supported || [],
                    encryptionRequired: reqEnc.encryptionRequired ?? reqEnc.encryption_required ?? false,
                };
            }

            // Deep mapping for response encryption (including legacy flat structure migration)
            if (data.credential_response_encryption || data.credentialResponseEncryption) {
                const resEnc = data.credentialResponseEncryption || data.credential_response_encryption;
                mappedData.credentialResponseEncryption = {
                    algValuesSupported: resEnc.algValuesSupported || resEnc.alg_values_supported || [],
                    encValuesSupported: resEnc.encValuesSupported || resEnc.enc_values_supported || [],
                    encryptionRequired: resEnc.encryptionRequired ?? resEnc.encryption_required ?? false,
                };
            } else if (data.credential_response_encryption_alg_values_supported) {
                // Handle even older flat structure
                mappedData.credentialResponseEncryption = {
                    algValuesSupported: data.credential_response_encryption_alg_values_supported || [],
                    encValuesSupported: data.credential_response_encryption_enc_values_supported || [],
                    encryptionRequired: data.require_credential_response_encryption || false
                };
            }

            setMetadata(mappedData);
            setError(null);
        } catch (err) {
            console.warn('Failed to fetch metadata (normal if not yet configured):', err);
        } finally {
            setLoading(false);
        }
    };

    const validateMetadata = (): boolean => {
        // Validation: encValuesSupported must contain at least 1 item
        if (metadata.credentialRequestEncryption.encValuesSupported.length === 0) {
            setError('Credential Request Encryption must have at least one supported enc value.');
            return false;
        }
        if (metadata.credentialResponseEncryption.encValuesSupported.length === 0) {
            setError('Credential Response Encryption must have at least one supported enc value.');
            return false;
        }

        // Validation: values must not be empty if encryption is required
        if (metadata.credentialRequestEncryption.encryptionRequired && metadata.credentialRequestEncryption.encValuesSupported.some(v => v.trim() === '')) {
            setError('Credential Request Encryption values cannot be empty when encryption is required.');
            return false;
        }
        if (metadata.credentialResponseEncryption.encryptionRequired && metadata.credentialResponseEncryption.encValuesSupported.some(v => v.trim() === '')) {
            setError('Credential Response Encryption values cannot be empty when encryption is required.');
            return false;
        }

        return true;
    };

    const handleSave = async () => {
        if (!metadata) return;
        if (!validateMetadata()) return;

        try {
            setLoading(true);
            await updateMetadata(metadata);
            setIsEditing(false);
            setSuccess('Metadata saved successfully.');
            setError(null);
            setTimeout(() => setSuccess(null), 3000);
        } catch (err) {
            console.error('Failed to update metadata:', err);
            setError('Failed to save metadata.');
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        setIsEditing(false);
        setError(null);
        fetchMetadata();
    };

    const handleChange = (field: keyof IssuerMetadata, value: any) => {
        if (!metadata) return;
        
        let newMetadata = { ...metadata, [field]: value };
        
        if (field === 'credentialIssuer') {
            const base = (value as string).replace(/\/$/, '');
            newMetadata.credentialOfferEndpoint = `${base}/credential-offer`;
            newMetadata.credentialEndpoint = `${base}/credential`;
            newMetadata.nonceEndpoint = `${base}/nonce`;
            newMetadata.deferredCredentialEndpoint = `${base}/deferred_credential`;
            newMetadata.notificationEndpoint = `${base}/notification`;
        }
        
        setMetadata(newMetadata);
    };

    const handleNestedChange = (objField: 'credentialRequestEncryption' | 'credentialResponseEncryption', subField: string, value: any) => {
        if (!metadata) return;
        setMetadata({
            ...metadata,
            [objField]: {
                ...metadata[objField],
                [subField]: value
            }
        });
    };

    const handleListChange = (field: keyof IssuerMetadata, index: number, value: string) => {
        if (!metadata) return;
        const newList = [...(metadata[field] as string[])];
        newList[index] = value;
        setMetadata({ ...metadata, [field]: newList });
    };

    const handleNestedListChange = (objField: 'credentialRequestEncryption' | 'credentialResponseEncryption', subField: 'algValuesSupported' | 'encValuesSupported', index: number, value: string) => {
        if (!metadata) return;
        const targetObj = metadata[objField] as any;
        const newList = [...targetObj[subField]];
        newList[index] = value;
        setMetadata({
            ...metadata,
            [objField]: {
                ...targetObj,
                [subField]: newList
            }
        });
    };

    const handleAddItem = (field: keyof IssuerMetadata) => {
        if (!metadata) return;
        setMetadata({ ...metadata, [field]: [...(metadata[field] as string[]), ''] });
    };

    const handleNestedAddItem = (objField: 'credentialRequestEncryption' | 'credentialResponseEncryption', subField: 'algValuesSupported' | 'encValuesSupported') => {
        if (!metadata) return;
        const targetObj = metadata[objField] as any;
        setMetadata({
            ...metadata,
            [objField]: {
                ...targetObj,
                [subField]: [...targetObj[subField], '']
            }
        });
    };

    const handleRemoveItem = (field: keyof IssuerMetadata, index: number) => {
        if (!metadata) return;
        const newList = [...(metadata[field] as string[])];
        newList.splice(index, 1);
        setMetadata({ ...metadata, [field]: newList });
    };

    const handleNestedRemoveItem = (objField: 'credentialRequestEncryption' | 'credentialResponseEncryption', subField: 'algValuesSupported' | 'encValuesSupported', index: number) => {
        if (!metadata) return;
        const targetObj = metadata[objField] as any;
        const newList = [...targetObj[subField]];
        newList.splice(index, 1);
        setMetadata({
            ...metadata,
            [objField]: {
                ...targetObj,
                [subField]: newList
            }
        });
    };

    const renderEncryptionSection = (title: string, objField: 'credentialRequestEncryption' | 'credentialResponseEncryption', showAlg: boolean = false) => {
        const data = metadata[objField];
        return (
            <Grid item xs={12} lg={6}>
                <FieldLabel>{title}</FieldLabel>
                <SectionPaper variant="outlined">
                    <Grid container spacing={3}>
                        {showAlg && (
                            <Grid item xs={12}>
                                <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1 }}>Alg values supported</Typography>
                                {(data as any).algValuesSupported.map((val: string, idx: number) => (
                                    <Box key={idx} sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                                        {!isEditing ? <Typography variant="body2" sx={{ pl: 1 }}>- {val}</Typography> : (
                                            <>
                                                <TextField
                                                    fullWidth
                                                    size="small"
                                                    value={val}
                                                    onChange={(e) => handleNestedListChange(objField, 'algValuesSupported', idx, e.target.value)}
                                                />
                                                <IconButton size="small" color="error" onClick={() => handleNestedRemoveItem(objField, 'algValuesSupported', idx)}>
                                                    <DeleteIcon fontSize="small" />
                                                </IconButton>
                                            </>
                                        )}
                                    </Box>
                                ))}
                                {isEditing && (
                                    <Button size="small" startIcon={<AddIcon />} onClick={() => handleNestedAddItem(objField, 'algValuesSupported')}>Add Alg</Button>
                                )}
                            </Grid>
                        )}

                        <Grid item xs={12}>
                            <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1 }}>Enc values supported</Typography>
                            {data.encValuesSupported.map((val, idx) => (
                                <Box key={idx} sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                                    {!isEditing ? <Typography variant="body2" sx={{ pl: 1 }}>- {val}</Typography> : (
                                        <>
                                            <TextField
                                                fullWidth
                                                size="small"
                                                value={val}
                                                onChange={(e) => handleNestedListChange(objField, 'encValuesSupported', idx, e.target.value)}
                                                error={isEditing && data.encryptionRequired && val.trim() === ''}
                                                helperText={isEditing && data.encryptionRequired && val.trim() === '' ? 'Value is required' : ''}
                                            />
                                            <IconButton size="small" color="error" onClick={() => handleNestedRemoveItem(objField, 'encValuesSupported', idx)}>
                                                <DeleteIcon fontSize="small" />
                                            </IconButton>
                                        </>
                                    )}
                                </Box>
                            ))}
                            {isEditing && (
                                <Button size="small" startIcon={<AddIcon />} onClick={() => handleNestedAddItem(objField, 'encValuesSupported')}>Add Enc</Button>
                            )}
                        </Grid>

                        <Grid item xs={12}>
                            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>Encryption Required</Typography>
                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                    <Typography variant="caption" sx={{ mr: 1, fontWeight: data.encryptionRequired ? 400 : 700}}>OFF</Typography>
                                    <Switch
                                        checked={data.encryptionRequired}
                                        onChange={(e) => handleNestedChange(objField, 'encryptionRequired', e.target.checked)}
                                        disabled={!isEditing}
                                    />
                                    <Typography variant="caption" sx={{ ml: 1, fontWeight: data.encryptionRequired ? 700 : 400}}>ON</Typography>
                                </Box>
                            </Box>
                        </Grid>
                    </Grid>
                </SectionPaper>
            </Grid>
        );
    };

    return (
        <>
            <FullscreenLoader open={loading} />
            <StyledContainer>
                <Typography variant="overline" color="primary" sx={{ fontWeight: 700 }}>
                    OID4VCI Management
                </Typography>
                <SectionHeader>
                    <StyledTitle sx={{ mb: 0 }}>Issuer Metadata</StyledTitle>
                    {!isEditing ? (
                        <Button variant="contained" startIcon={<EditIcon />} onClick={() => setIsEditing(true)}>Edit</Button>
                    ) : (
                        <Box sx={{ display: 'flex', gap: 1 }}>
                            <Button variant="contained" color="primary" startIcon={<SaveIcon />} onClick={handleSave} disabled={loading}>Save</Button>
                            <Button variant="outlined" color="error" startIcon={<CancelIcon />} onClick={handleCancel}>Cancel</Button>
                        </Box>
                    )}
                </SectionHeader>

                {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}
                {success && <Alert severity="success" sx={{ mb: 3 }}>{success}</Alert>}

                {metadata && (
                    <Grid container spacing={4}>
                        {renderEncryptionSection('Credential Request Encryption', 'credentialRequestEncryption')}
                        {renderEncryptionSection('Credential Response Encryption', 'credentialResponseEncryption', true)}

                        <Grid item xs={12}>
                            <Divider sx={{ my: 2 }} />
                            <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 2 }}>Endpoints</Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Credential Issuer (Main Endpoint)"
                                        value={metadata.credentialIssuer}
                                        onChange={(e) => handleChange('credentialIssuer', e.target.value)}
                                        slotProps={{ input: { readOnly: !isEditing } }}
                                        variant={isEditing ? 'outlined' : 'standard'}
                                        size="small"
                                        placeholder="http://127.0.0.1:8090"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <FieldLabel sx={{ mb: 0.5 }}>Authorization Servers</FieldLabel>
                                    {metadata.authorizationServer.map((url, idx) => (
                                        <Box key={idx} sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                                            <TextField
                                                fullWidth
                                                size="small"
                                                value={url}
                                                onChange={(e) => handleListChange('authorizationServer', idx, e.target.value)}
                                                slotProps={{ input: { readOnly: !isEditing } }}
                                                variant={isEditing ? 'outlined' : 'standard'}
                                                helperText="URL of the OIDC Authorization Server"
                                            />
                                            {isEditing && (
                                                <IconButton size="small" color="error" onClick={() => handleRemoveItem('authorizationServer', idx)}>
                                                    <DeleteIcon fontSize="small" />
                                                </IconButton>
                                            )}
                                        </Box>
                                    ))}
                                    {isEditing && (
                                        <Button size="small" startIcon={<AddIcon />} onClick={() => handleAddItem('authorizationServer')}>Add Server</Button>
                                    )}
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <TextField
                                        fullWidth
                                        label="Credential Offer Endpoint"
                                        value={metadata.credentialOfferEndpoint}
                                        slotProps={{ input: { readOnly: true } }}
                                        variant={isEditing ? 'outlined' : 'standard'}
                                        size="small"
                                        helperText="Fixed endpoint for the credential offer"
                                        disabled
                                    />
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <TextField
                                        fullWidth
                                        label="Credential Endpoint"
                                        value={metadata.credentialEndpoint}
                                        slotProps={{ input: { readOnly: true } }}
                                        variant={isEditing ? 'outlined' : 'standard'}
                                        size="small"
                                        helperText="Fixed endpoint to request credentials"
                                        disabled
                                    />
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <TextField
                                        fullWidth
                                        label="Nonce Endpoint"
                                        value={metadata.nonceEndpoint}
                                        slotProps={{ input: { readOnly: true } }}
                                        variant={isEditing ? 'outlined' : 'standard'}
                                        size="small"
                                        helperText="Fixed endpoint to request a fresh nonce"
                                        disabled
                                    />
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <TextField
                                        fullWidth
                                        label="Deferred Credential Endpoint"
                                        value={metadata.deferredCredentialEndpoint}
                                        slotProps={{ input: { readOnly: true } }}
                                        variant={isEditing ? 'outlined' : 'standard'}
                                        size="small"
                                        helperText="Fixed endpoint for deferred issuance"
                                        disabled
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Notification Endpoint"
                                        value={metadata.notificationEndpoint}
                                        slotProps={{ input: { readOnly: true } }}
                                        variant={isEditing ? 'outlined' : 'standard'}
                                        size="small"
                                        helperText="Fixed endpoint for transaction notifications"
                                        disabled
                                    />
                                </Grid>
                            </Grid>
                        </Grid>
                    </Grid>
                )}
            </StyledContainer>
        </>
    );
}
