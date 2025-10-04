// TDE2 - RESOLUCAO DE PROBLEMAS ESTRUTURADOS EM COMPUTACAO - ARVORE B DE ORDEM M
// ALUNO: JOAQUIM DOS ANJOS FARACO

public class ArvoreB {

    private final int ordem;
    private Node raiz;
    private int totalChaves;

    public ArvoreB(int ordem) {
        this.ordem = ordem;
        this.raiz = new Node(ordem, true);
        this.totalChaves = 0;
    }

    // ================ METODOS DE BUSCA ===============================
    public boolean buscar(int chave) {
        return buscarRecursivo(raiz, chave);
    }

    private boolean buscarRecursivo(Node no, int chave) {
        int i = 0;
        
        while (i < no.getNumChaves() && chave > no.getChaves()[i]) {
            i++;
        }

        if (i < no.getNumChaves() && chave == no.getChaves()[i]) {
            return true;
        }

        if (no.isEhFolha()) {
            return false;
        }

        return buscarRecursivo(no.getPonteiros()[i], chave);
    }
    // =======================================================================

    // =================== METODOS DE INSERCAO ===============================
    public void inserir(int chave) {
        Node r = raiz;

        if (r.getNumChaves() == ordem - 1) {
            Node novaRaiz = new Node(ordem, false);
            novaRaiz.getPonteiros()[0] = r;
            dividirFilho(novaRaiz, 0, r);
            raiz = novaRaiz;
            inserirNaoCheio(novaRaiz, chave);
        } else {
            inserirNaoCheio(r, chave);
        }
        totalChaves++;
    }

    private void inserirNaoCheio(Node no, int chave) {
        int i = no.getNumChaves() - 1;

        if (no.isEhFolha()) {

            while (i >= 0 && chave < no.getChaves()[i]) {
                no.getChaves()[i + 1] = no.getChaves()[i];
                i--;
            }
            no.getChaves()[i + 1] = chave;
            no.setNumChaves(no.getNumChaves() + 1);
        } else {

            while (i >= 0 && chave < no.getChaves()[i]) {
                i--;
            }
            i++;

            if (no.getPonteiros()[i].getNumChaves() == ordem - 1) {
                dividirFilho(no, i, no.getPonteiros()[i]);
                if (chave > no.getChaves()[i]) {
                    i++;
                }
            }
            inserirNaoCheio(no.getPonteiros()[i], chave);
        }
    }

    private void dividirFilho(Node pai, int indice, Node filho) {
        int meio = (ordem - 1) / 2;
        Node novoNo = new Node(ordem, filho.isEhFolha());
        novoNo.setNumChaves(ordem - 1 - meio - 1);

        for (int j = 0; j < novoNo.getNumChaves(); j++) {
            novoNo.getChaves()[j] = filho.getChaves()[j + meio + 1];
        }

        if (!filho.isEhFolha()) {
            for (int j = 0; j <= novoNo.getNumChaves(); j++) {
                novoNo.getPonteiros()[j] = filho.getPonteiros()[j + meio + 1];
            }
        }

        filho.setNumChaves(meio);

        for (int j = pai.getNumChaves(); j > indice; j--) {
            pai.getPonteiros()[j + 1] = pai.getPonteiros()[j];
        }
        pai.getPonteiros()[indice + 1] = novoNo;

        for (int j = pai.getNumChaves() - 1; j >= indice; j--) {
            pai.getChaves()[j + 1] = pai.getChaves()[j];
        }
        pai.getChaves()[indice] = filho.getChaves()[meio];
        pai.setNumChaves(pai.getNumChaves() + 1);
    }
    // =================================================================================

    // ============================= METODOS REMOCAO ====================================
    public void remover(int chave) {
        removerRecursivo(raiz, chave);
    
        if (raiz.getNumChaves() == 0) {
            if (!raiz.isEhFolha()) {
                raiz = raiz.getPonteiros()[0];
            }
        }
        totalChaves--;
    }

    private void removerRecursivo(Node no, int chave) {
        int i = 0;
        while (i < no.getNumChaves() && chave > no.getChaves()[i]) {
            i++;
        }

        if (i < no.getNumChaves() && chave == no.getChaves()[i]) {
            if (no.isEhFolha()) {
                removerDaFolha(no, i);
            } else {
                removerDoInterno(no, i);
            }
        } else if (!no.isEhFolha()) {
            boolean ehUltimo = (i == no.getNumChaves());
            
            if (no.getPonteiros()[i].getNumChaves() < ordem / 2) {
                preencher(no, i);
            }

            if (ehUltimo && i > no.getNumChaves()) {
                removerRecursivo(no.getPonteiros()[i - 1], chave);
            } else {
                removerRecursivo(no.getPonteiros()[i], chave);
            }
        }
    }

    private void removerDaFolha(Node no, int indice) {
        for (int i = indice + 1; i < no.getNumChaves(); i++) {
            no.getChaves()[i - 1] = no.getChaves()[i];
        }
        no.setNumChaves(no.getNumChaves() - 1);
    }

    private void removerDoInterno(Node no, int indice) {
        int chave = no.getChaves()[indice];

        if (no.getPonteiros()[indice].getNumChaves() >= ordem / 2) {
            int predecessor = getPredecessor(no, indice);
            no.getChaves()[indice] = predecessor;
            removerRecursivo(no.getPonteiros()[indice], predecessor);
        } else if (no.getPonteiros()[indice + 1].getNumChaves() >= ordem / 2) {
            int sucessor = getSucessor(no, indice);
            no.getChaves()[indice] = sucessor;
            removerRecursivo(no.getPonteiros()[indice + 1], sucessor);
        } else {
            merge(no, indice);
            removerRecursivo(no.getPonteiros()[indice], chave);
        }
    }

    private int getPredecessor(Node no, int indice) {
        Node atual = no.getPonteiros()[indice];
        while (!atual.isEhFolha()) {
            atual = atual.getPonteiros()[atual.getNumChaves()];
        }
        return atual.getChaves()[atual.getNumChaves() - 1];
    }

    private int getSucessor(Node no, int indice) {
        Node atual = no.getPonteiros()[indice + 1];
        while (!atual.isEhFolha()) {
            atual = atual.getPonteiros()[0];
        }
        return atual.getChaves()[0];
    }

    private void preencher(Node no, int indice) {
        if (indice != 0 && no.getPonteiros()[indice - 1].getNumChaves() >= ordem / 2) {
            pegarDoAnterior(no, indice);
        } else if (indice != no.getNumChaves() && no.getPonteiros()[indice + 1].getNumChaves() >= ordem / 2) {
            pegarDoProximo(no, indice);
        } else {
            if (indice != no.getNumChaves()) {
                merge(no, indice);
            } else {
                merge(no, indice - 1);
            }
        }
    }

    private void pegarDoAnterior(Node no, int indice) {
        Node filho = no.getPonteiros()[indice];
        Node irmao = no.getPonteiros()[indice - 1];

        for (int i = filho.getNumChaves() - 1; i >= 0; i--) {
            filho.getChaves()[i + 1] = filho.getChaves()[i];
        }

        if (!filho.isEhFolha()) {
            for (int i = filho.getNumChaves(); i >= 0; i--) {
                filho.getPonteiros()[i + 1] = filho.getPonteiros()[i];
            }
        }

        filho.getChaves()[0] = no.getChaves()[indice - 1];

        if (!filho.isEhFolha()) {
            filho.getPonteiros()[0] = irmao.getPonteiros()[irmao.getNumChaves()];
        }

        no.getChaves()[indice - 1] = irmao.getChaves()[irmao.getNumChaves() - 1];
        filho.setNumChaves(filho.getNumChaves() + 1);
        irmao.setNumChaves(irmao.getNumChaves() - 1);
    }

    private void pegarDoProximo(Node no, int indice) {
        Node filho = no.getPonteiros()[indice];
        Node irmao = no.getPonteiros()[indice + 1];

        filho.getChaves()[filho.getNumChaves()] = no.getChaves()[indice];

        if (!filho.isEhFolha()) {
            filho.getPonteiros()[filho.getNumChaves() + 1] = irmao.getPonteiros()[0];
        }

        no.getChaves()[indice] = irmao.getChaves()[0];

        for (int i = 1; i < irmao.getNumChaves(); i++) {
            irmao.getChaves()[i - 1] = irmao.getChaves()[i];
        }

        if (!irmao.isEhFolha()) {
            for (int i = 1; i <= irmao.getNumChaves(); i++) {
                irmao.getPonteiros()[i - 1] = irmao.getPonteiros()[i];
            }
        }

        filho.setNumChaves(filho.getNumChaves() + 1);
        irmao.setNumChaves(irmao.getNumChaves() - 1);
    }

    private void merge(Node no, int indice) {
        Node filho = no.getPonteiros()[indice];
        Node irmao = no.getPonteiros()[indice + 1];

        filho.getChaves()[filho.getNumChaves()] = no.getChaves()[indice];

        for (int i = 0; i < irmao.getNumChaves(); i++) {
            filho.getChaves()[filho.getNumChaves() + 1 + i] = irmao.getChaves()[i];
        }

        if (!filho.isEhFolha()) {
            for (int i = 0; i <= irmao.getNumChaves(); i++) {
                filho.getPonteiros()[filho.getNumChaves() + 1 + i] = irmao.getPonteiros()[i];
            }
        }

        for (int i = indice + 1; i < no.getNumChaves(); i++) {
            no.getChaves()[i - 1] = no.getChaves()[i];
        }

        for (int i = indice + 2; i <= no.getNumChaves(); i++) {
            no.getPonteiros()[i - 1] = no.getPonteiros()[i];
        }

        filho.setNumChaves(filho.getNumChaves() + irmao.getNumChaves() + 1);
        no.setNumChaves(no.getNumChaves() - 1);

    }
    // ==============================================================================================

    // =================== METODOS IMPRESSAO ========================================================
    public void imprimirArvore() {
        impressaoRecursiva(this.raiz, 0);
    }

    private void impressaoRecursiva(Node no, int nivel) {
        System.out.print("Nivel " + nivel + ": ");
        for (int i = 0; i < no.getNumChaves(); i++) {
            System.out.print(no.getChaves()[i] + " ");
        }
        System.out.println();

        if (!no.isEhFolha()) {
            for (int i = 0; i <= no.getNumChaves(); i++) {
                if (no.getPonteiros()[i] != null) {
                    impressaoRecursiva(no.getPonteiros()[i], nivel + 1);
                }
            }
        }
    }
    // =================================================================================================

    // ============= IMPLEMENTACAO NODE ================================================================
    private static class Node {
        private final int ordem;
        private int numChaves;
        private boolean ehFolha;
        private int[] chaves;
        private Node[] ponteiros;

        public Node(int ordem, boolean ehFolha) {

            this.ordem = ordem;
            this.chaves = new int[ordem - 1];
            this.ponteiros = new Node[ordem];
            this.numChaves = 0;
            this.ehFolha = ehFolha;

        }   

        public int getNumChaves() {
            return numChaves;
        }

        public boolean isEhFolha() {
            return ehFolha;
        }

        public int[] getChaves() {
            return chaves;
        }

        public Node[] getPonteiros() {
            return ponteiros;
        }

        public void setNumChaves(int num) {
            this.numChaves = num;
        }

        public void setEhFolha(boolean folha) {
            this.ehFolha = folha;
        }

    }
    // ==================================================================================================

    // ============================ MAIN PARA TESTES ====================================================
    public static void main(String[] args) {

        ArvoreB arvore_teste = new ArvoreB(3);

        int[] valores = {10, 20, 5, 6, 12, 30, 7, 17};
        for (int valor : valores) {
            arvore_teste.inserir(valor);
            System.out.println(valor + " foi inserido");
        }

        System.out.println("\n\nArvore apos as insercoes:\n");
        arvore_teste.imprimirArvore();

        System.out.println("\n\nBuscar id 6: " + arvore_teste.buscar(6));
        System.out.println("Buscar id 15: " + arvore_teste.buscar(15));

        arvore_teste.remover(6);
        System.out.println("\n\nArvore apos remover 6:");
        arvore_teste.imprimirArvore();

    }
    // ========================================================================================================

}