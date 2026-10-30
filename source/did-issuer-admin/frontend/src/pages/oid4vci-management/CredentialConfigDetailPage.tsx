import { Box, Button, IconButton, TextField, Typography, styled, Alert, MenuItem, Checkbox, Accordion, AccordionSummary, AccordionDetails, FormControl, InputLabel, Select, ListItemText, FormHelperText, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';
import React, { useEffect, useState, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router';
import { getCredentialConfigs, updateCredentialConfigs, SdkCredentialConfig } from '../../apis/oid4vci-api';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteIcon from '@mui/icons-material/Delete';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

const StyledContainer = styled(Box)(({ theme }) => ({
    width: 'min(800px, calc(100% - 32px))',
    boxSizing: 'border-box',
    margin: 'auto',
    marginTop: theme.spacing(1),
    padding: theme.spacing(3),
    borderRadius: theme.shape.borderRadius,
    backgroundColor: '#ffffff',
    boxShadow: '0px 4px 8px 0px #0000001A',
}));

const StyledTitle = styled(Typography)({
    textAlign: 'left',
    fontSize: '24px',
    fontWeight: 700,
});

const StyledInputArea = styled(Box)(({ theme }) => ({
    marginTop: theme.spacing(2),
}));

const SectionTitle = styled(Typography)(({ theme }) => ({
    fontSize: theme.typography.h6.fontSize,
    fontWeight: theme.typography.fontWeightRegular,
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(1),
}));

const MSO_MDOC_FORMAT = 'mso_mdoc-did';
const SD_JWT_FORMAT = 'dc+sd-jwt-did';
const KNOWN_BINDING_METHODS = ['jwk', 'cose_key'];
const OTHER_BINDING_METHOD = '__other__';

type Metadata = Record<string, any>;

const publishedFormat = (sdkFormat: string) =>
    sdkFormat.startsWith('mso_mdoc') || sdkFormat.startsWith('mso-mdoc') ? 'mso_mdoc' : 'dc+sd-jwt';

const typeKey = (sdkFormat: string) => publishedFormat(sdkFormat) === 'mso_mdoc' ? 'doctype' : 'vct';

const newMetadata = (sdkFormat: string): Metadata => ({
    format: publishedFormat(sdkFormat),
    [typeKey(sdkFormat)]: '',
    credential_signing_alg_values_supported: publishedFormat(sdkFormat) === 'mso_mdoc' ? [-7] : ['ES256'],
    cryptographic_binding_methods_supported: ['jwk'],
    proof_types_supported: { jwt: { proof_signing_alg_values_supported: ['ES256'] } },
    credential_metadata: { display: [{ locale: 'en', name: '' }] }
});

const parseMetadata = (value: string): Metadata | null => {
    try {
        const parsed = JSON.parse(value);
        return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? parsed : null;
    } catch {
        return null;
    }
};

const validateMetadata = (value: string, sdkFormat: string): string | null => {
    const metadata = parseMetadata(value);
    if (!metadata) return 'Metadata JSON must be a valid JSON object.';
    if (metadata.format !== publishedFormat(sdkFormat)) {
        return `JSON format must be "${publishedFormat(sdkFormat)}" for the selected SDK format.`;
    }
    if (typeof metadata[typeKey(sdkFormat)] !== 'string' || !metadata[typeKey(sdkFormat)].trim()) {
        return `${typeKey(sdkFormat)} is required and must match the credential being issued.`;
    }
    const bindings = metadata.cryptographic_binding_methods_supported;
    if (bindings !== undefined && (!Array.isArray(bindings) || bindings.some((item: unknown) => typeof item !== 'string' || !item.trim()))) {
        return 'Cryptographic binding methods must contain valid selected values.';
    }
    const claims = metadata.credential_metadata?.claims;
    if (claims !== undefined && (!Array.isArray(claims) || claims.length === 0 ||
        claims.some((claim: any) => !Array.isArray(claim?.path) || claim.path.length === 0 ||
            claim.path.some((part: unknown) => typeof part === 'string' && !part.trim())))) {
        return 'Claims must be a non-empty list, and every claim needs a path.';
    }
    return null;
};

export default function CredentialConfigDetailPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const isEdit = !!id;

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [configs, setConfigs] = useState<Record<string, SdkCredentialConfig>>({});

    const [configId, setConfigId] = useState(id || '');
    const [format, setFormat] = useState(MSO_MDOC_FORMAT);
    const [identifiers, setIdentifiers] = useState<string[]>([]);
    const [metadataJson, setMetadataJson] = useState('');
    const [advancedOpen, setAdvancedOpen] = useState(false);
    const [showValidation, setShowValidation] = useState(false);
    const [showCustomBinding, setShowCustomBinding] = useState(false);
    const [customBindingInput, setCustomBindingInput] = useState('');
    const metadata = useMemo(() => parseMetadata(metadataJson), [metadataJson]);
    const metadataError = useMemo(() => metadataJson ? validateMetadata(metadataJson, format) : null, [metadataJson, format]);
    const claims: any[] = Array.isArray(metadata?.credential_metadata?.claims) ? metadata.credential_metadata.claims : [];
    const bindingMethods: string[] = Array.isArray(metadata?.cryptographic_binding_methods_supported)
        ? metadata.cryptographic_binding_methods_supported.filter((value: unknown): value is string => typeof value === 'string')
        : [];
    const bindingOptions = [...KNOWN_BINDING_METHODS, ...bindingMethods.filter(value => !KNOWN_BINDING_METHODS.includes(value))];

    const changeMetadata = (change: (draft: Metadata) => void) => {
        const draft = parseMetadata(metadataJson);
        if (!draft) return;
        change(draft);
        setMetadataJson(JSON.stringify(draft, null, 2));
        setError(null);
    };

    const changeClaim = (index: number, change: (claim: any) => void) => {
        changeMetadata(draft => {
            const claim = draft.credential_metadata.claims[index];
            change(claim);
        });
    };

    useEffect(() => {
        fetchData();
    }, [id]);

    const fetchData = async () => {
        try {
            setLoading(true);
            const configsRes = await getCredentialConfigs();
            const allConfigs = configsRes.data;
            
            setConfigs(allConfigs);

            if (isEdit && id && allConfigs[id]) {
                const config = allConfigs[id];
                setFormat(config.format || '');
                setIdentifiers(config.identifiers || []);
                setMetadataJson(config.metadataJson || '{}');
            } else {
                // Initialize default for new registration
                setMetadataJson(JSON.stringify(newMetadata(MSO_MDOC_FORMAT), null, 2));
            }
            setError(null);
            setShowValidation(false);
            setShowCustomBinding(false);
            setCustomBindingInput('');
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

    const handleReset = () => {
        setConfigId('');
        setFormat(MSO_MDOC_FORMAT);
        setIdentifiers([]);
        setMetadataJson(JSON.stringify(newMetadata(MSO_MDOC_FORMAT), null, 2));
        setAdvancedOpen(false);
        setShowCustomBinding(false);
        setCustomBindingInput('');
        setShowValidation(false);
        setError(null);
    };

    const handleSave = async () => {
        setShowValidation(true);
        if (!configId.trim()) {
            setError('ID is required.');
            return;
        }

        if (!isEdit && configs[configId]) {
            setError('This ID already exists.');
            return;
        }
        if (!identifiers.some(identifier => identifier.trim())) {
            setError('Add at least one SDK identifier.');
            return;
        }
        if (metadataError) {
            setError(metadataError);
            return;
        }

        try {
            setLoading(true);

            // 1. Prepare SDK Config Map
            const updatedConfigs = { ...configs };
            updatedConfigs[configId] = {
                id: configId,
                format,
                identifiers: identifiers.filter(i => i.trim() !== ''),
                metadataJson: metadataJson
            };

            await updateCredentialConfigs(updatedConfigs);

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
            <Typography variant="h4">OID4VCI Management</Typography>
            <StyledContainer>
                <StyledTitle>
                    {isEdit ? 'Credential Config Detail' : 'Register New Configuration'}
                </StyledTitle>

                {error && <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>{error}</Alert>}

                <StyledInputArea>
                    <SectionTitle sx={{ mt: 0 }}>Basic Information</SectionTitle>
                    <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                        <TextField
                            label="Configuration ID"
                            required
                            size="small"
                            margin="normal"
                            sx={{ width: { xs: '100%', sm: '60%' } }}
                            value={configId}
                            onChange={(e) => setConfigId(e.target.value)}
                            disabled={isEdit}
                            variant="outlined"
                            placeholder="e.g. StudentID"
                            helperText="Example: StudentID — a unique configuration ID."
                        />
                        <TextField
                            select
                            label="Format"
                            required
                            size="small"
                            margin="normal"
                            sx={{ width: { xs: '100%', sm: '60%' } }}
                            value={format || MSO_MDOC_FORMAT}
                            onChange={(e) => {
                                const nextFormat = e.target.value;
                                const previousType = typeKey(format);
                                setFormat(nextFormat);
                                changeMetadata(draft => {
                                    draft.format = publishedFormat(nextFormat);
                                    if (previousType !== typeKey(nextFormat)) {
                                        delete draft[previousType];
                                        draft[typeKey(nextFormat)] = '';
                                        draft.credential_signing_alg_values_supported = publishedFormat(nextFormat) === 'mso_mdoc' ? [-7] : ['ES256'];
                                    }
                                });
                            }}
                            variant="outlined"
                            helperText="Example: mso_mdoc-did for an mdoc, or dc+sd-jwt-did for an SD-JWT VC."
                        >
                            <MenuItem value={MSO_MDOC_FORMAT}>{MSO_MDOC_FORMAT}</MenuItem>
                            <MenuItem value={SD_JWT_FORMAT}>{SD_JWT_FORMAT}</MenuItem>
                            {format && format !== MSO_MDOC_FORMAT && format !== SD_JWT_FORMAT && (
                                <MenuItem value={format} style={{ display: 'none' }}>{format}</MenuItem>
                            )}
                        </TextField>
                    </Box>

                    <SectionTitle>Identifiers (SDK)</SectionTitle>
                    <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                        Example SDK identifier: mDocPID for an mdoc, or StudentId for an SD-JWT VC.
                    </Typography>
                    <Button variant="contained" startIcon={<AddCircleOutlineIcon />} sx={{ mt: 2, mb: 2 }} onClick={handleAddIdentifier}>
                        Add Identifier
                    </Button>
                    <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: 'auto' }}>
                        <Table size="small">
                            <TableHead>
                                <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
                                    <TableCell>Identifier *</TableCell>
                                    <TableCell align="center" sx={{ width: 90 }}>Delete</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {identifiers.map((identifier, index) => (
                                    <TableRow key={index}>
                                        <TableCell>
                                            <TextField fullWidth size="small" value={identifier}
                                                onChange={(e) => handleIdentifierChange(index, e.target.value)}
                                                placeholder={typeKey(format) === 'doctype' ? 'e.g. mDocPID' : 'e.g. StudentId'} />
                                        </TableCell>
                                        <TableCell align="center">
                                            <IconButton aria-label={`Remove identifier ${index + 1}`} color="error" onClick={() => handleRemoveIdentifier(index)}>
                                                <DeleteIcon sx={{ color: '#FF8400' }} />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>

                    <SectionTitle>Credential Metadata</SectionTitle>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                        These fields are published in issuer metadata. Use the credential's actual type and claim paths.
                    </Typography>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField
                            label={typeKey(format) === 'doctype' ? 'Document type (doctype)' : 'Credential type (vct)'}
                            required size="small" sx={{ width: { xs: '100%', sm: '60%' } }} value={metadata?.[typeKey(format)] ?? ''}
                            disabled={!metadata}
                            placeholder={typeKey(format) === 'doctype' ? 'e.g. eu.europa.ec.eudi.pid.1' : 'e.g. urn:eudi:pid:1'}
                            onChange={e => changeMetadata(draft => { draft[typeKey(format)] = e.target.value; })}
                            helperText={typeKey(format) === 'doctype' ? 'Example: eu.europa.ec.eudi.pid.1. Use the exact mdoc document type.' : 'Example: urn:eudi:pid:1. Use the exact SD-JWT VC type identifier.'}
                        />
                        <TextField
                            label="Display name" size="small" sx={{ width: { xs: '100%', sm: '60%' } }}
                            value={metadata?.credential_metadata?.display?.[0]?.name ?? ''}
                            disabled={!metadata}
                            placeholder={typeKey(format) === 'doctype' ? 'e.g. PID (MSO Mdoc)' : 'e.g. PID (SD-JWT VC)'}
                            helperText={typeKey(format) === 'doctype'
                                ? 'Example: PID (MSO Mdoc) — shown to wallet users.'
                                : 'Example: PID (SD-JWT VC) — shown to wallet users.'}
                            onChange={e => changeMetadata(draft => {
                                draft.credential_metadata ??= {};
                                if (!Array.isArray(draft.credential_metadata.display) || !draft.credential_metadata.display.length) {
                                    draft.credential_metadata.display = [{ locale: 'en', name: '' }];
                                }
                                draft.credential_metadata.display[0].name = e.target.value;
                            })}
                        />
                        <TextField
                            label="Scope (optional)" size="small" sx={{ width: { xs: '100%', sm: '60%' } }} value={metadata?.scope ?? ''}
                            disabled={!metadata}
                            placeholder={typeKey(format) === 'doctype' ? 'e.g. eu.europa.ec.eudi.pid_mdoc' : 'e.g. eu.europa.ec.eudi.pid_vc_sd_jwt'}
                            helperText={typeKey(format) === 'doctype'
                                ? 'Example: eu.europa.ec.eudi.pid_mdoc. Leave blank when scope is not used.'
                                : 'Example: eu.europa.ec.eudi.pid_vc_sd_jwt. Leave blank when scope is not used.'}
                            onChange={e => changeMetadata(draft => {
                                if (e.target.value) draft.scope = e.target.value;
                                else delete draft.scope;
                            })}
                        />
                        <FormControl size="small" sx={{ width: { xs: '100%', sm: '60%' } }} disabled={!metadata}>
                            <InputLabel id="binding-methods-label">Cryptographic binding methods</InputLabel>
                            <Select<string[]>
                                labelId="binding-methods-label"
                                multiple
                                value={bindingMethods}
                                label="Cryptographic binding methods"
                                renderValue={selected => selected.join(', ')}
                                onChange={e => {
                                    const selected = typeof e.target.value === 'string' ? e.target.value.split(',') : e.target.value;
                                    if (selected.includes(OTHER_BINDING_METHOD)) setShowCustomBinding(true);
                                    changeMetadata(draft => {
                                        draft.cryptographic_binding_methods_supported = selected.filter(value => value !== OTHER_BINDING_METHOD);
                                    });
                                }}
                            >
                                {bindingOptions.map(method => (
                                    <MenuItem key={method} value={method}>
                                        <Checkbox checked={bindingMethods.includes(method)} />
                                        <ListItemText primary={method} />
                                    </MenuItem>
                                ))}
                                <MenuItem value={OTHER_BINDING_METHOD}>
                                    <AddCircleOutlineIcon fontSize="small" sx={{ mr: 1 }} />
                                    <ListItemText primary="Other — add a method" />
                                </MenuItem>
                            </Select>
                            <FormHelperText>Examples: jwk, cose_key. Select Other to add another method.</FormHelperText>
                        </FormControl>
                        {showCustomBinding && (
                            <Box sx={{ display: 'flex', gap: 1, alignItems: 'flex-start' }}>
                                <TextField
                                    label="Other binding method" size="small" sx={{ width: { xs: '100%', sm: '60%' } }}
                                    value={customBindingInput}
                                    onChange={e => setCustomBindingInput(e.target.value)}
                                    placeholder="e.g. did:key"
                                    helperText="Enter the binding method supported by your issuer and wallet."
                                />
                                <Button variant="outlined" disabled={!customBindingInput.trim()}
                                    onClick={() => {
                                        const method = customBindingInput.trim();
                                        if (!bindingMethods.includes(method)) {
                                            changeMetadata(draft => {
                                                draft.cryptographic_binding_methods_supported = [...bindingMethods, method];
                                            });
                                        }
                                        setCustomBindingInput('');
                                        setShowCustomBinding(false);
                                    }}>Add</Button>
                            </Box>
                        )}
                    </Box>

                    <SectionTitle>Claims</SectionTitle>
                    <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                        Example path: given_name, or eu.europa.ec.eudi.pid.1 / given_name for mdoc. Label: Given Name(s). Value type: string. Check Required for claims always included.
                    </Typography>
                    <Button variant="contained" startIcon={<AddCircleOutlineIcon />} disabled={!metadata} sx={{ mt: 2, mb: 2 }}
                        onClick={() => changeMetadata(draft => {
                            draft.credential_metadata ??= {};
                            draft.credential_metadata.claims ??= [];
                            draft.credential_metadata.claims.push({ path: [''], mandatory: false, display: [{ locale: 'en', name: '' }] });
                        })}>Add Claim</Button>
                    <TableContainer component={Paper} sx={{ maxHeight: 400, overflow: 'auto' }}>
                        <Table size="small" sx={{ minWidth: 720 }}>
                            <TableHead>
                                <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
                                    <TableCell sx={{ width: 240 }}>Path *</TableCell>
                                    <TableCell sx={{ width: 180 }}>Label</TableCell>
                                    <TableCell sx={{ width: 110 }}>Required</TableCell>
                                    <TableCell sx={{ width: 130 }}>Value type</TableCell>
                                    <TableCell align="center" sx={{ width: 80 }}>Delete</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                    {claims.map((claim, index) => (
                        <TableRow key={index}>
                            <TableCell sx={{ verticalAlign: 'top' }}><TextField size="small" fullWidth
                                value={Array.isArray(claim.path) ? claim.path.join(' / ') : ''}
                                placeholder={typeKey(format) === 'doctype' ? 'e.g. eu.europa.ec.eudi.pid.1 / given_name' : 'e.g. given_name'}
                                onChange={e => changeClaim(index, item => { item.path = e.target.value.split('/').map(part => part.trim()); })} /></TableCell>
                            <TableCell sx={{ verticalAlign: 'top' }}><TextField size="small" fullWidth
                                value={claim.display?.[0]?.name ?? ''}
                                placeholder="e.g. Given Name(s)"
                                onChange={e => changeClaim(index, item => {
                                    if (!Array.isArray(item.display) || !item.display.length) {
                                        item.display = [{ locale: 'en', name: '' }];
                                    }
                                    item.display[0].name = e.target.value;
                                })} /></TableCell>
                            <TableCell sx={{ verticalAlign: 'top' }}><Checkbox aria-label={`Claim ${index + 1} required`} checked={claim.mandatory === true}
                                onChange={e => changeClaim(index, item => { item.mandatory = e.target.checked; })} /></TableCell>
                            <TableCell sx={{ verticalAlign: 'top' }}><TextField size="small" fullWidth
                                value={claim.value_type ?? ''}
                                placeholder="e.g. string"
                                onChange={e => changeClaim(index, item => {
                                    if (e.target.value) item.value_type = e.target.value;
                                    else delete item.value_type;
                                })} /></TableCell>
                            <TableCell align="center" sx={{ verticalAlign: 'top' }}><IconButton aria-label={`Remove claim ${index + 1}`} color="error"
                                onClick={() => changeMetadata(draft => {
                                    draft.credential_metadata.claims.splice(index, 1);
                                    if (!draft.credential_metadata.claims.length) delete draft.credential_metadata.claims;
                                })}>
                                <DeleteIcon sx={{ color: '#FF8400' }} />
                            </IconButton></TableCell>
                        </TableRow>
                    ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <Accordion expanded={advancedOpen} onChange={(_, expanded) => setAdvancedOpen(expanded)} sx={{ mt: 3 }}>
                        <AccordionSummary expandIcon={<ExpandMoreIcon />}>Advanced: Metadata JSON</AccordionSummary>
                        <AccordionDetails>
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                                Edit additional fields here. Form edits preserve fields not shown above.
                            </Typography>
                            <TextField multiline minRows={12} fullWidth value={metadataJson}
                                onChange={e => { setMetadataJson(e.target.value); setError(null); }}
                                slotProps={{ input: { sx: { fontFamily: 'monospace', fontSize: 13 } } }} />
                        </AccordionDetails>
                    </Accordion>
                    {showValidation && metadataError && <Alert severity="error" sx={{ mt: 2 }}>{metadataError}</Alert>}

                    <Box sx={{ display: 'flex', justifyContent: 'center', flexWrap: 'wrap', gap: 2, mt: 3 }}>
                        <Button variant="contained" color="primary" onClick={handleSave}>
                            {isEdit ? 'Save' : 'Register'}
                        </Button>
                        {!isEdit && <Button variant="contained" color="secondary" onClick={handleReset}>Reset</Button>}
                        <Button variant="outlined" color="secondary" onClick={() => navigate('/oid4vci-management/credential-config')}>
                            Cancel
                        </Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    );
}
