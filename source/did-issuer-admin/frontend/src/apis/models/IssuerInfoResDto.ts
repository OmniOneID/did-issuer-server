import { IssuerStatus } from '../constants/IssuerStatus';

export interface IssuerInfoResDto {
  id: number;
  did: string;
  name: string;
  status: IssuerStatus;
  serverUrl: string;
  certificateUrl: string;
  didDocument?: any;
  createdAt: string;
  updatedAt: string;
}
