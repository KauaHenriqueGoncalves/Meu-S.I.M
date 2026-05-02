export interface TokenPayload {
  id: string | null | undefined;
  scope: string | null | undefined;
  sub: string | null | undefined;
  exp: number | null | undefined;
}