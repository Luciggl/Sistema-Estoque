package Model.services;

import Model.entities.Products;
import Model.enums.Category;
import Model.exceptions.ProdutoJaExisteException;
import Model.exceptions.ProdutoNaoExisteException;

import java.io.*;
import java.util.ArrayList;

public class Estoque implements Model.repositories.Estoque {
    private ArrayList<Products> productsEstoque = new ArrayList<>();

    public Estoque() {}

    public boolean produtoExiste(Products product) {
        return productsEstoque.contains(product);
    }

    public void addEstoque(Products product) throws ProdutoJaExisteException {
        if (produtoExiste(product)) {
            throw new ProdutoJaExisteException("Error: Produto já existe no estoque");
        } else {
            productsEstoque.add(product);
        }
    }

    @Override
    public void removeEstoque(Products product, int id) throws ProdutoNaoExisteException {
        boolean productFound = false;

        for (Products p : productsEstoque) {
            if (p.getId() == id) {
                productFound = true;
                productsEstoque.remove(p);
                break;
            }
        }
        if (!productFound) {
            throw new ProdutoNaoExisteException("Error: Produto não existe no estoque");
        }
    }

    @Override
    public void AddQuant(Products product, int quant) throws ProdutoNaoExisteException {
        if (produtoExiste(product)) {
            int newQuant = product.getQuanti() + quant;
            product.setQuanti(newQuant);
        } else {
            throw new ProdutoNaoExisteException("Error: Produto não existe no estoque");
        }
    }

    @Override
    public void removeQuant(Products product, int quant) throws ProdutoNaoExisteException {
        if (produtoExiste(product)) {
            if (product.getQuanti() >= quant) {
                int newQuant = product.getQuanti() - quant;
                product.setQuanti(newQuant);
            } else {
                throw new ProdutoNaoExisteException("Error: Produto não existe no estoque");
            }
        } else {
            throw new ProdutoNaoExisteException("Error: Produto não existe no estoque");
        }
    }

    public Products getProductById(double id) {
        for (Products product : productsEstoque) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public ArrayList<Products> getProductsByCategory(Category category) {
        ArrayList<Products> productsByCategory = new ArrayList<>();
        for (Products product : productsEstoque) {
            if (product.getCategory() == category) {
                productsByCategory.add(product);
            }
        }
        return productsByCategory;
    }

    @Override
    public String toString() {
        return productsEstoque + "\n";
    }

    public void salvarEstoque(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Products product : productsEstoque) {
                writer.write(
                        product.getId() + "," +
                                product.getName() + "," +
                                product.getManufacturer() + "," +
                                product.getCategory() + "," +
                                product.getValue() + "," +
                                product.getQuanti());
                writer.newLine();
            }

            System.out.println("Estoque salvo com sucesso no arquivo: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void carregarEstoque(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            productsEstoque = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 6) {
                    double id = Double.parseDouble(parts[0]);
                    String name = parts[1];
                    String manufacturer = parts[2];
                    Category category = Category.valueOf(parts[3]);
                    double value = Double.parseDouble(parts[4]);
                    int quant = Integer.parseInt(parts[5]);

                    Products product = new Products(id, name,manufacturer,category,value, quant );
                    productsEstoque.add(product);
                }
            }
            System.out.println("Estoque carregado com sucesso do arquivo: " + filePath);
        } catch (IOException e) {
            System.out.println("Estoque Vazio");
        }
    }

    public double calcularValorTotalEstoque() {
        double valorTotal = 0;

        for (Products product : productsEstoque) {
            valorTotal += product.getValue() * product.getQuanti();
        }

        return valorTotal;
    }
}
