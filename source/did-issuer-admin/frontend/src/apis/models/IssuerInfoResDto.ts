import { TasStatus } from '../constants/IssuerStatus';

export interface IssuerInfoResDto {
  id: number;
  did: string;
  name: string;
  status: TasStatus;
  serverUrl: string;
  certificateUrl: string;
  didDocument?: any;
  createdAt: string;
  updatedAt: string;
}
