import styled from '@emotion/styled';

import { theme } from '@/style/theme';

export const LikeButtonContainer = styled.button<{ isLiked: boolean }>`
  cursor: pointer;

  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 4.5rem;
  height: 2.5rem;
  padding: 0 0.75rem;

  color: ${({ isLiked }) => (isLiked ? theme.color.light.analogous_primary_400 : 'white')};

  border: 1px solid
    ${({ isLiked }) => (isLiked ? theme.color.light.analogous_primary_400 : theme.color.light.secondary_800)};
  border-radius: 16px;
`;
