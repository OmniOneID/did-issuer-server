import {
  Box,
  FormControlLabel,
  Radio,
  RadioGroup,
  Typography,
  styled
} from "@mui/material";
import { FC, useMemo } from "react";

export type UserQueryType = 'DB' | 'TEST';

interface Props {
  value: UserQueryType;
  onChange?: (value: UserQueryType) => void;
  readOnly?: boolean;
}

const UserQuerySection: FC<Props> = ({ value, onChange, readOnly = false }) => {
  const StyledInputArea = useMemo(
    () => styled(Box)(({ theme }) => ({ marginTop: theme.spacing(2) })),
    []
  );

  return (
    <StyledInputArea>
      <Typography variant="h6" fontWeight="bold" gutterBottom>
        User Query Method
      </Typography>

      <RadioGroup
        row
        value={value ?? 'DB'}
        onChange={(e) => onChange?.(e.target.value as UserQueryType)}
      >
        <FormControlLabel value="DB" control={<Radio disabled={readOnly} />} label="DB" />
        <FormControlLabel value="TEST" control={<Radio disabled={readOnly} />} label="TEST" />
      </RadioGroup>
    </StyledInputArea>
  );
};

export default UserQuerySection;
