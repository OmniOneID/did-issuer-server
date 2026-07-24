import { Alert, Box, Button, Chip, Paper, TextField, Typography } from '@mui/material';
import { useDialogs } from '@toolpad/core';
import { useEffect, useMemo, useState } from 'react';
import {
    getListProviderRegistration,
    ListProviderRegistration,
    requestListProviderRegistration,
} from '../../apis/oid4vci-api';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../components/dialog/CustomDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../utils/error-handler';

type FieldErrors = {
    credentialIssuerMetadataUri?: string;
    userInitiationUri?: string;
};

const isWebUri = (value: string) => {
    try {
        const uri = new URL(value);
        return (uri.protocol === 'http:' || uri.protocol === 'https:')
            && Boolean(uri.hostname) && !uri.username && !uri.password;
    } catch {
        return false;
    }
};

export default function ListProviderRegistrationPage() {
    const dialogs = useDialogs();
    const [loading, setLoading] = useState(true);
    const [registration, setRegistration] = useState<ListProviderRegistration | null>(null);
    const [metadataUri, setMetadataUri] = useState('');
    const [userInitiationUri, setUserInitiationUri] = useState('');
    const [errors, setErrors] = useState<FieldErrors>({});

    const requested = Boolean(registration?.registrationId);

    const load = async () => {
        setLoading(true);
        try {
            const { data } = await getListProviderRegistration();
            const value = data as ListProviderRegistration;
            setRegistration(value);
            setMetadataUri(value.credentialIssuerMetadataUri || '');
            setUserInitiationUri(value.userInitiationUri || '');
        } catch (error) {
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Unable to load registration',
                message: formatErrorMessage(error, 'Failed to load List Provider registration.'),
                isModal: true,
            });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        load();
    }, []);

    const validate = () => {
        const next: FieldErrors = {};
        if (!isWebUri(metadataUri)) {
            next.credentialIssuerMetadataUri = 'Enter a valid HTTP or HTTPS Metadata URI.';
        }
        if (!isWebUri(userInitiationUri)) {
            next.userInitiationUri = 'Enter a valid HTTP or HTTPS User Initiation URI.';
        }
        setErrors(next);
        return Object.keys(next).length === 0;
    };

    const requestRegistration = async () => {
        if (!registration || !validate()) return;
        const confirmed = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: `Request registration for ${registration.credentialIssuer} from ${registration.listProviderUrl}?`,
            isModal: true,
        });
        if (!confirmed) return;

        setLoading(true);
        try {
            const { data } = await requestListProviderRegistration({
                credentialIssuer: registration.credentialIssuer,
                credentialIssuerMetadataUri: metadataUri.trim(),
                userInitiationUri: userInitiationUri.trim(),
            });
            const value = data as ListProviderRegistration;
            setRegistration(value);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Registration requested',
                message: `The List Provider accepted the request with status ${value.status}.`,
                isModal: true,
            });
        } catch (error) {
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Registration failed',
                message: formatErrorMessage(error, 'Failed to request Credential Issuer registration.'),
                isModal: true,
            });
        } finally {
            setLoading(false);
        }
    };

    const statusColor = useMemo(() => {
        if (registration?.status === 'ACTIVE') return 'success';
        if (registration?.status === 'REJECTED' || registration?.status === 'REVOKED') return 'error';
        return 'warning';
    }, [registration?.status]);

    return (
        <>
            <FullscreenLoader open={loading} />
            <Paper sx={{ width: '100%', maxWidth: 1120, mx: 'auto', mt: 1, p: 3 }}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Typography fontSize={24} fontWeight={700}>List Provider Registration</Typography>
                    {registration?.status && (
                        <Chip label={registration.status} color={statusColor} variant="outlined" />
                    )}
                </Box>

                {requested && (
                    <Alert severity="info" sx={{ mb: 2 }}>
                        Registration request #{registration?.registrationId} was submitted at {registration?.requestedAt}.
                        Approval is managed by the List Provider.
                    </Alert>
                )}

                <Box display="grid" gap={2}>
                    <TextField
                        label="List Provider URL"
                        value={registration?.listProviderUrl || ''}
                        InputProps={{ readOnly: true }}
                    />
                    <TextField
                        label="Credential Issuer"
                        value={registration?.credentialIssuer || ''}
                        InputProps={{ readOnly: true }}
                        helperText="Loaded from the Issuer DID."
                    />
                    <TextField
                        label="Credential Issuer Metadata URI *"
                        value={metadataUri}
                        onChange={(event) => {
                            setMetadataUri(event.target.value);
                            setErrors((current) => ({ ...current, credentialIssuerMetadataUri: undefined }));
                        }}
                        error={Boolean(errors.credentialIssuerMetadataUri)}
                        helperText={errors.credentialIssuerMetadataUri}
                        disabled={requested}
                    />
                    <TextField
                        label="User Initiation URI *"
                        value={userInitiationUri}
                        onChange={(event) => {
                            setUserInitiationUri(event.target.value);
                            setErrors((current) => ({ ...current, userInitiationUri: undefined }));
                        }}
                        error={Boolean(errors.userInitiationUri)}
                        helperText={errors.userInitiationUri}
                        disabled={requested}
                    />
                    <Box display="flex" justifyContent="flex-end" gap={1}>
                        <Button onClick={load} disabled={loading}>Refresh</Button>
                        <Button
                            variant="contained"
                            onClick={requestRegistration}
                            disabled={loading || requested || !registration?.credentialIssuer}
                        >
                            Request Registration
                        </Button>
                    </Box>
                </Box>
            </Paper>
        </>
    );
}
