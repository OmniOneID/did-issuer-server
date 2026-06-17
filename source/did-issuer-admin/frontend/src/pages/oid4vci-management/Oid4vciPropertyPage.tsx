import { Box, Button, TextField, Typography, styled, Alert, useTheme, Grid, Paper, IconButton, Divider } from '@mui/material';
import React, { useEffect, useState, useMemo } from 'react';
import { getProperties, updateProperties, Oid4vcProperty, DEFAULT_PROPERTY, ClientPlatform } from '../../apis/oid4vci-api';
import SaveIcon from '@mui/icons-material/Save';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import FullscreenLoader from '../../components/loading/FullscreenLoader';

export default function Oid4vciPropertyPage() {
    const theme = useTheme();
    const [properties, setProperties] = useState<Oid4vcProperty>(DEFAULT_PROPERTY);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
        margin: 'auto',
        marginTop: theme.spacing(1),
        padding: theme.spacing(3),
        borderRadius: theme.shape.borderRadius,
        backgroundColor: '#ffffff',
        boxShadow: '0px 4px 8px 0px #0000001A',
    })), []);

    const StyledTitle = useMemo(() => styled(Typography)({
        textAlign: 'left',
        fontSize: '24px',
        fontWeight: 700,
        marginBottom: '24px',
    }), []);

    const SectionPaper = useMemo(() => styled(Paper)(({ theme }) => ({
        padding: theme.spacing(2),
        backgroundColor: '#f8fafc',
        borderRadius: theme.shape.borderRadius,
        border: '1px solid #e2e8f0',
        marginBottom: theme.spacing(3),
    })), []);

    const FieldLabel = useMemo(() => styled(Typography)(({ theme }) => ({
        fontWeight: 700,
        fontSize: '14px',
        color: theme.palette.text.secondary,
        marginBottom: theme.spacing(1),
    })), []);

    useEffect(() => {
        fetchProperties();
    }, []);

    const fetchProperties = async () => {
        try {
            setLoading(true);
            const response = await getProperties();
            setProperties({ ...DEFAULT_PROPERTY, ...response.data });
            setError(null);
        } catch (err) {
            console.warn('Failed to fetch properties (normal if not yet configured):', err);
        } finally {
            setLoading(false);
        }
    };

    const handleSave = async () => {
        try {
            setLoading(true);
            await updateProperties(properties);
            setSuccess('Properties saved successfully.');
            setError(null);
            setTimeout(() => setSuccess(null), 3000);
        } catch (err) {
            console.error('Failed to update properties:', err);
            setError('Failed to save properties.');
        } finally {
            setLoading(false);
        }
    };

    const updateNestedField = (path: string, value: any) => {
        const newProperties = { ...properties };
        const keys = path.split('.');
        let current: any = newProperties;
        for (let i = 0; i < keys.length - 1; i++) {
            if (!current[keys[i]]) current[keys[i]] = {};
            current[keys[i]] = { ...current[keys[i]] };
            current = current[keys[i]];
        }
        current[keys[keys.length - 1]] = value;
        setProperties(newProperties);
    };

    const handleListAdd = (path: string) => {
        const keys = path.split('.');
        let current: any = properties;
        for (const key of keys) {
            current = current[key];
        }
        const newList = [...(current || []), ''];
        updateNestedField(path, newList);
    };

    const handleListChange = (path: string, index: number, value: string) => {
        const keys = path.split('.');
        let current: any = properties;
        for (const key of keys) {
            current = current[key];
        }
        const newList = [...(current || [])];
        newList[index] = value;
        updateNestedField(path, newList);
    };

    const handleListRemove = (path: string, index: number) => {
        const keys = path.split('.');
        let current: any = properties;
        for (const key of keys) {
            current = current[key];
        }
        const newList = [...(current || [])];
        newList.splice(index, 1);
        updateNestedField(path, newList);
    };

    const renderListField = (label: string, path: string, items: string[] | undefined) => (
        <Grid item xs={12}>
            <FieldLabel>{label}</FieldLabel>
            {(items || []).map((item, idx) => (
                <Box key={idx} sx={{ display: 'flex', gap: 1, mb: 1 }}>
                    <TextField
                        fullWidth
                        size="small"
                        value={item}
                        onChange={(e) => handleListChange(path, idx, e.target.value)}
                    />
                    <IconButton color="error" size="small" onClick={() => handleListRemove(path, idx)}>
                        <DeleteIcon fontSize="small" />
                    </IconButton>
                </Box>
            ))}
            <Button size="small" startIcon={<AddIcon />} onClick={() => handleListAdd(path)}>Add {label}</Button>
        </Grid>
    );

    return (
        <>
            <FullscreenLoader open={loading} />
            <StyledContainer>
                <Typography variant="overline" color="primary" sx={{ fontWeight: 700 }}>
                    OID4VCI Management
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                    <StyledTitle sx={{ mb: 0 }}>Property</StyledTitle>
                    <Button
                        variant="contained"
                        startIcon={<SaveIcon />}
                        onClick={handleSave}
                        disabled={loading}
                    >
                        Save
                    </Button>
                </Box>

                {error && <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>{error}</Alert>}
                {success && <Alert severity="success" sx={{ mb: 3 }} onClose={() => setSuccess(null)}>{success}</Alert>}

                <SectionPaper variant="outlined">
                    <Typography variant="h6" sx={{ mb: 2, fontWeight: 700 }}>Main Client Settings</Typography>
                    <Grid container spacing={3}>
                        <Grid item xs={12} md={6}>
                            <TextField
                                fullWidth
                                label="Client ID"
                                value={properties.clients?.clientId || ''}
                                onChange={(e) => updateNestedField('clients.clientId', e.target.value)}
                                size="small"
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                fullWidth
                                label="Client Secret"
                                type="password"
                                value={properties.clients?.clientSecret || ''}
                                onChange={(e) => updateNestedField('clients.clientSecret', e.target.value)}
                                size="small"
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Redirect URL"
                                value={properties.clients?.redirectUrl || ''}
                                onChange={(e) => updateNestedField('clients.redirectUrl', e.target.value)}
                                size="small"
                                placeholder="${issuer.base-url}/auth/callback"
                            />
                        </Grid>
                        {renderListField('Redirect URIs', 'clients.redirectUris', properties.clients?.redirectUris)}
                        {renderListField('Scopes', 'clients.scopes', properties.clients?.scopes)}
                    </Grid>
                </SectionPaper>

                <Grid container spacing={3}>
                    <Grid item xs={12} md={6}>
                        <SectionPaper variant="outlined">
                            <Typography variant="h6" sx={{ mb: 2, fontWeight: 700 }}>Android Client</Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Client ID"
                                        value={properties.clients?.android?.clientId || ''}
                                        onChange={(e) => updateNestedField('clients.android.clientId', e.target.value)}
                                        size="small"
                                    />
                                </Grid>
                                {renderListField('Redirect URIs', 'clients.android.redirectUris', properties.clients?.android?.redirectUris)}
                                {renderListField('Scopes', 'clients.android.scopes', properties.clients?.android?.scopes)}
                            </Grid>
                        </SectionPaper>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <SectionPaper variant="outlined">
                            <Typography variant="h6" sx={{ mb: 2, fontWeight: 700 }}>iOS Client</Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Client ID"
                                        value={properties.clients?.ios?.clientId || ''}
                                        onChange={(e) => updateNestedField('clients.ios.clientId', e.target.value)}
                                        size="small"
                                    />
                                </Grid>
                                {renderListField('Redirect URIs', 'clients.ios.redirectUris', properties.clients?.ios?.redirectUris)}
                                {renderListField('Scopes', 'clients.ios.scopes', properties.clients?.ios?.scopes)}
                            </Grid>
                        </SectionPaper>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <SectionPaper variant="outlined">
                            <Typography variant="h6" sx={{ mb: 2, fontWeight: 700 }}>Android OpenID Client</Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Client ID"
                                        value={properties.clients?.androidOpenid?.clientId || ''}
                                        onChange={(e) => updateNestedField('clients.androidOpenid.clientId', e.target.value)}
                                        size="small"
                                    />
                                </Grid>
                                {renderListField('Redirect URIs', 'clients.androidOpenid.redirectUris', properties.clients?.androidOpenid?.redirectUris)}
                                {renderListField('Scopes', 'clients.androidOpenid.scopes', properties.clients?.androidOpenid?.scopes)}
                            </Grid>
                        </SectionPaper>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <SectionPaper variant="outlined">
                            <Typography variant="h6" sx={{ mb: 2, fontWeight: 700 }}>iOS OpenID Client</Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Client ID"
                                        value={properties.clients?.iosOpenid?.clientId || ''}
                                        onChange={(e) => updateNestedField('clients.iosOpenid.clientId', e.target.value)}
                                        size="small"
                                    />
                                </Grid>
                                {renderListField('Redirect URIs', 'clients.iosOpenid.redirectUris', properties.clients?.iosOpenid?.redirectUris)}
                                {renderListField('Scopes', 'clients.iosOpenid.scopes', properties.clients?.iosOpenid?.scopes)}
                            </Grid>
                        </SectionPaper>
                    </Grid>
                </Grid>
            </StyledContainer>
        </>
    );
}
