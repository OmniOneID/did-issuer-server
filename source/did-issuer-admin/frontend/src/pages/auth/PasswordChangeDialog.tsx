import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    TextField,
    Box,
    Typography,
    IconButton,
    Collapse,
    List,
    ListItem,
    ListItemIcon,
    ListItemText
} from "@mui/material";
import React, {useEffect, useState} from "react";
import {usePasswordPolicy} from "../../hooks/usePasswordPolicy";
import {ValidationRuleResult} from "../../constants/password-policy";
import {Visibility, VisibilityOff, Check, Close, ExpandMore, ExpandLess, Info} from "@mui/icons-material";

interface PasswordResetDialogProps {
    open: boolean;
    onClose: () => void;
    onSubmit: (oldPassword: string, newPassword: string) => void;
}

interface ErrorState {
    oldPassword?: string;
    newPassword?: string;
    confirmPassword?: string;
}

const PasswordChangeDialog: React.FC<PasswordResetDialogProps> = ({open, onClose, onSubmit}) => {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errors, setErrors] = useState<ErrorState>({});
    const [isButtonDisabled, setIsButtonDisabled] = useState(true);

    // Password visibility states
    const [showOldPassword, setShowOldPassword] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    // Requirements visibility
    const [showRequirements, setShowRequirements] = useState(false);

    // Focus and touch states for better UX
    const [newPasswordTouched, setNewPasswordTouched] = useState(false);
    const [confirmPasswordTouched, setConfirmPasswordTouched] = useState(false);

    // Password policy hook
    const {
        policy,
        isLoading: isPolicyLoading,
        validatePassword,
        getValidationResults,
        getValidationSummary
    } = usePasswordPolicy();

    // Real-time password validation results
    const [validationResults, setValidationResults] = useState<ValidationRuleResult[]>([]);
    const [validationSummary, setValidationSummary] = useState({
        isValid: false,
        passedCount: 0,
        totalCount: 0,
        failedRules: [] as string[]
    });

    const handleConfirm = () => {
        if (!validate()) return;
        onSubmit(oldPassword, newPassword);
        onClose();
    };

    const handleChange = (setter: React.Dispatch<React.SetStateAction<string>>) =>
        (event: React.ChangeEvent<HTMLInputElement>) => setter(event.target.value);

    const handleNewPasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const password = event.target.value;
        setNewPassword(password);

        // Update real-time validation when policy is available
        if (policy && password) {
            const results = getValidationResults(password);
            const summary = getValidationSummary(password);
            setValidationResults(results);
            setValidationSummary(summary);

            // Auto-show requirements when user starts typing
            if (!showRequirements) {
                setShowRequirements(true);
            }

            // Auto-hide requirements when password becomes valid and is not focused
            if (summary.isValid && document.activeElement?.id !== 'new-password') {
                setTimeout(() => {
                    setShowRequirements(false);
                }, 1500);
            }
        } else {
            setValidationResults([]);
            setValidationSummary({
                isValid: false,
                passedCount: 0,
                totalCount: 0,
                failedRules: []
            });
        }
    };

    const handleNewPasswordBlur = () => {
        setNewPasswordTouched(true);
    };

    const handleConfirmPasswordBlur = () => {
        setConfirmPasswordTouched(true);
    };

    const validate = () => {
        let tempErrors: ErrorState = {};

        // Validate current password
        if (!oldPassword.trim()) {
            tempErrors.oldPassword = "Please enter your current password.";
        }

        // Validate new password using policy
        if (!newPassword.trim()) {
            tempErrors.newPassword = "Please enter a new password.";
        } else if (policy && newPasswordTouched && !validatePassword(newPassword)) {
            const summary = getValidationSummary(newPassword);
            tempErrors.newPassword = "Password does not meet policy requirements.";
        }

        // Validate password confirmation
        if (!confirmPassword.trim()) {
            tempErrors.confirmPassword = "Please confirm your new password.";
        } else if (confirmPasswordTouched && newPassword !== confirmPassword) {
            tempErrors.confirmPassword = "Passwords do not match.";
        }

        setErrors(tempErrors);
        return Object.values(tempErrors).every((error) => !error);
    };

    // Update button state based on validation
    useEffect(() => {
        const hasBasicInput = oldPassword.trim() && newPassword.trim() && confirmPassword.trim();
        const isPasswordValid = policy ? validatePassword(newPassword) : newPassword.length >= 8;
        const passwordsMatch = newPassword === confirmPassword;

        setIsButtonDisabled(!hasBasicInput || !isPasswordValid || !passwordsMatch);
    }, [oldPassword, newPassword, confirmPassword, policy, validatePassword]);

    // Reset form when dialog opens
    useEffect(() => {
        if (open) {
            setOldPassword("");
            setNewPassword("");
            setConfirmPassword("");
            setErrors({});
            setIsButtonDisabled(true);
            setValidationResults([]);
            setValidationSummary({
                isValid: false,
                passedCount: 0,
                totalCount: 0,
                failedRules: []
            });
            setShowOldPassword(false);
            setShowNewPassword(false);
            setShowConfirmPassword(false);
            setShowRequirements(false);
            setNewPasswordTouched(false);
            setConfirmPasswordTouched(false);
        }
    }, [open]);

    // Validate form on changes
    useEffect(() => {
        validate();
    }, [oldPassword, newPassword, confirmPassword, newPasswordTouched, confirmPasswordTouched]);

    // Show loading if policy is being loaded
    if (isPolicyLoading) {
        return (
            <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" sx={{maxWidth: 500, margin: "0 auto"}}>
                <DialogContent sx={{textAlign: 'center', py: 4}}>
                    <Typography>Loading password policy...</Typography>
                </DialogContent>
            </Dialog>
        );
    }

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" sx={{maxWidth: 500, margin: "0 auto"}}>
            <Box sx={{px: 2}}>
                <DialogTitle sx={{p: 0, pt: 2, fontWeight: 700}}>Change Password</DialogTitle>
                <Box sx={{height: "1px", backgroundColor: "var(--G40, #BFBFBF)", width: "100%", mt: 1}}/>
            </Box>

            <DialogContent sx={{px: 2, pt: 3}}>
                {/* Current Password */}
                <TextField
                    fullWidth
                    label="Current Password *"
                    type={showOldPassword ? "text" : "password"}
                    variant="outlined"
                    margin="normal"
                    value={oldPassword}
                    onChange={handleChange(setOldPassword)}
                    error={!!errors.oldPassword}
                    helperText={errors.oldPassword}
                    InputProps={{
                        endAdornment: (
                            <IconButton
                                aria-label="toggle password visibility"
                                onClick={() => setShowOldPassword(!showOldPassword)}
                                edge="end"
                            >
                                {showOldPassword ? <VisibilityOff /> : <Visibility />}
                            </IconButton>
                        ),
                    }}
                />

                {/* New Password */}
                <TextField
                    id="new-password"
                    fullWidth
                    label="New Password *"
                    type={showNewPassword ? "text" : "password"}
                    variant="outlined"
                    margin="normal"
                    value={newPassword}
                    onChange={handleNewPasswordChange}
                    onBlur={handleNewPasswordBlur}
                    error={!!errors.newPassword}
                    helperText={errors.newPassword}
                    InputProps={{
                        endAdornment: (
                            <IconButton
                                aria-label="toggle password visibility"
                                onClick={() => setShowNewPassword(!showNewPassword)}
                                edge="end"
                            >
                                {showNewPassword ? <VisibilityOff /> : <Visibility />}
                            </IconButton>
                        ),
                    }}
                />



                {/* Password Requirements Toggle */}
                {newPassword && policy && (
                    <Box sx={{ mt: 1 }}>
                        <Button
                            size="small"
                            variant="text"
                            color="inherit"
                            startIcon={<Info fontSize="small" />}
                            endIcon={showRequirements ? <ExpandLess /> : <ExpandMore />}
                            onClick={() => setShowRequirements(!showRequirements)}
                            sx={{
                                textTransform: 'none',
                                fontSize: '0.875rem',
                                color: 'text.secondary',
                                p: 0.5,
                                minWidth: 'auto'
                            }}
                        >
                            Requirements ({validationSummary.passedCount}/{validationSummary.totalCount})
                        </Button>

                        {/* Password Requirements List */}
                        <Collapse in={showRequirements}>
                            <Box sx={{
                                mt: 1,
                                backgroundColor: 'rgba(0,0,0,0.02)',
                                borderRadius: 1,
                                border: '1px solid rgba(0,0,0,0.1)'
                            }}>
                                <List dense sx={{ py: 1 }}>
                                    {validationResults.map((result, index) => (
                                        <ListItem key={index} sx={{ py: 0.25, px: 2 }}>
                                            <ListItemIcon sx={{ minWidth: 32 }}>
                                                {result.isValid ? (
                                                    <Check
                                                        fontSize="small"
                                                        sx={{ color: 'success.main' }}
                                                    />
                                                ) : (
                                                    <Close
                                                        fontSize="small"
                                                        sx={{ color: 'text.disabled' }}
                                                    />
                                                )}
                                            </ListItemIcon>
                                            <ListItemText
                                                primary={result.message}
                                                slotProps={{
                                                    primary: {
                                                        variant: 'body2',
                                                        sx: {
                                                            color: result.isValid ? 'success.main' : 'text.secondary',
                                                            fontSize: '0.875rem'
                                                        }
                                                    }
                                                }}
                                            />
                                        </ListItem>
                                    ))}
                                </List>
                            </Box>
                        </Collapse>
                    </Box>
                )}

                {/* Confirm Password */}
                <TextField
                    fullWidth
                    label="Confirm Password *"
                    type={showConfirmPassword ? "text" : "password"}
                    variant="outlined"
                    margin="normal"
                    value={confirmPassword}
                    onChange={handleChange(setConfirmPassword)}
                    onBlur={handleConfirmPasswordBlur}
                    error={!!errors.confirmPassword}
                    helperText={errors.confirmPassword}
                    InputProps={{
                        endAdornment: (
                            <IconButton
                                aria-label="toggle password visibility"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                edge="end"
                            >
                                {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                            </IconButton>
                        ),
                    }}
                />
            </DialogContent>

            <DialogActions sx={{px: 2, pt: 0, display: "flex", gap: 2, mt: 0}}>
                <Button variant="outlined" onClick={onClose} color="primary" sx={{flexGrow: 1, height: "48px"}}>
                    Cancel
                </Button>
                <Button variant="contained" onClick={handleConfirm} color="primary" disabled={isButtonDisabled}
                        sx={{flexGrow: 1, height: "48px"}}>
                    Update
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default PasswordChangeDialog;
