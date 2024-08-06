import styled from '@emotion/styled';

import { theme } from '@/style/theme';

export const HeaderContainer = styled.nav`
  position: fixed;
  z-index: 1;
  left: 0;

  display: flex;
  justify-content: center;

  width: 100%;

  background: white;
  border-bottom: 2px solid ${theme.color.light.secondary_200};
`;

export const HeaderContentContainer = styled.div`
  display: flex;
  gap: 3.75rem;
  align-items: center;

  width: 80rem;
  max-width: 80rem;
  height: 4rem;
  padding: 1.875rem;

  white-space: nowrap;
`;

export const NavOptionButton = styled.button`
  cursor: pointer;
  background: none;
`;

export const UserMenuButton = styled.button`
  cursor: pointer;

  width: 2.375rem;
  height: 2.375rem;

  color: ${theme.color.light.primary_800};

  object-fit: contain;
  background-color: white;
  border-radius: 50%;
`;